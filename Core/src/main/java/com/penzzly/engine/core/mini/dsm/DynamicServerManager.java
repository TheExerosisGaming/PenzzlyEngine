package com.penzzly.engine.core.mini.dsm;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.penzzly.engine.core.mini.dsm.ByteUtils.*;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.nio.file.Files.*;

public class DynamicServerManager {
	public static final Number OP_ONLINE = 0;
	public static final Number OP_SHUTDOWN = 1;
	public static final Number OP_ERROR = 2;
	public static final String ERR_ENABLE = "\tThe server returned an error code while enabling.\n\tCode: %s";
	private final BiMap<UUID, SocketAddress> servers = HashBiMap.create();
	private final DatagramConnection connection;
	private final Supplier<Single<Number>> spawner;
	
	public DynamicServerManager(DatagramConnection connection, Supplier<Single<Number>> spawner) {
		this.spawner = spawner;
		this.connection = connection;
		
		connection.receive(OP_ERROR, (request, address) -> {
			System.err.println(getString(request));
			servers.inverse().remove(address);
		});
	}
	
	public static DynamicServerManager createExample(Path root, Path template, Supplier<Number> ports) throws Exception {
		checkArgument(exists(root) && isDirectory(root), "Root must be a directory.");
		checkArgument(exists(template) && isReadable(template), "Template must be a file.");
		
		return new DynamicServerManager(new DatagramConnection(12345, 512), () -> Single.fromCallable(() -> {
			Number port = ports.get().intValue();
			Path directory = root.resolve(valueOf(port));
			createDirectories(directory);
			unzip(newInputStream(template), directory);
			return port;
		}));
	}
	
	public Single<UUID> startup() {
		return spawner.get().flatMap(port -> Single.create(observer ->
				connection.reply(OP_ONLINE, new InetSocketAddress(port.intValue()), request -> response -> {
					UUID id = UUID.randomUUID();
					if (request.get() != 0)
						observer.onError(new IllegalStateException(format(ERR_ENABLE, getString(request))));
					putString(response, id);
					observer.onSuccess(id);
				}))
		);
	}
	
	public Completable shutdown(UUID server, boolean force) {
		return Completable.create(observer ->
				connection.request(OP_SHUTDOWN, servers.get(server), request -> {
					request.put((byte) (force ? 1 : 0));
					return response -> servers.remove(server);
				})
		);
	}
	
	
	public DatagramConnection getConnection() {
		return connection;
	}
}