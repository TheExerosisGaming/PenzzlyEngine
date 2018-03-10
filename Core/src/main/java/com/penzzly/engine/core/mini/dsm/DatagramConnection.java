package com.penzzly.engine.core.mini.dsm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.DatagramChannel.open;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class DatagramConnection implements Closeable {
	private final Multimap<Number, BiConsumer<ByteBuffer, SocketAddress>> handlers = HashMultimap.create();
	private final ExecutorService executor = newSingleThreadExecutor();
	private final DatagramChannel channel;
	private final ByteBuffer in;
	private final ByteBuffer out;
	
	public DatagramConnection(Number port, Number length) throws IOException {
		this(port, length, ByteBuffer::get);
	}
	
	public DatagramConnection(Number port, Number length, Function<ByteBuffer, Number> opcodes) throws IOException {
		in = allocate(length.intValue());
		out = allocate(length.intValue());
		in.clear();
		channel = open();
		channel.configureBlocking(true);
		channel.bind(new InetSocketAddress("localhost", port.intValue()));
		executor.submit(() -> {
			while (channel.isOpen()) {
				SocketAddress address = channel.receive(in);
				in.flip();
				handlers.get(opcodes.apply(in)).forEach(handler ->
						handler.accept(in, address)
				);
				in.clear();
			}
			return null;
		});
	}
	
	public void send(SocketAddress address, Number opcode, Consumer<ByteBuffer> packet) {
		try {
			synchronized (out) {
				out.clear();
				out.put(opcode.byteValue());
				packet.accept(out);
				out.flip();
				channel.send(out, address);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void receive(Number opcode, SocketAddress from, Consumer<ByteBuffer> handler) {
		receive(opcode, (response, address) -> {
			if (address.equals(from))
				handler.accept(response);
		});
	}
	
	public void receive(Number opcode, BiConsumer<ByteBuffer, SocketAddress> handler) {
		handlers.put(opcode, handler);
	}
	
	public void respond(Number opcode, Function<ByteBuffer, Consumer<ByteBuffer>> handler) {
		respond(opcode, (request, from) -> handler.apply(request));
	}
	
	public void respond(Number opcode, BiFunction<ByteBuffer, SocketAddress, Consumer<ByteBuffer>> handler) {
		receive(opcode, (request, from) -> {
			Consumer<ByteBuffer> response = handler.apply(request, from);
			send(from, opcode, response);
		});
	}
	
	public void reply(Number opcode, SocketAddress from, Function<ByteBuffer, Consumer<ByteBuffer>> handler) {
		receive(opcode, new BiConsumer<ByteBuffer, SocketAddress>() {
			@Override
			public void accept(ByteBuffer request, SocketAddress address) {
				if (!address.equals(from))
					return;
				handlers.remove(opcode.longValue(), this);
				Consumer<ByteBuffer> response = handler.apply(request);
				send(from, opcode, response);
			}
		});
	}
	
	public void request(Number opcode, SocketAddress to, Function<ByteBuffer, Consumer<ByteBuffer>> handler) {
		send(to, opcode, request -> {
			Consumer<ByteBuffer> callback = handler.apply(request);
			receive(opcode, new BiConsumer<ByteBuffer, SocketAddress>() {
				@Override
				public void accept(ByteBuffer response, SocketAddress address) {
					if (!address.equals(to))
						return;
					callback.accept(response);
					handlers.remove(opcode.longValue(), this);
				}
			});
		});
	}
	
	
	@Override
	public void close() throws IOException {
		channel.close();
		executor.shutdown();
	}
}
