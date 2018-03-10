package com.penzzly.engine.core.mini.dsm;

import io.reactivex.Single;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

import static com.penzzly.engine.core.mini.dsm.ByteUtils.getString;
import static com.penzzly.engine.core.mini.dsm.ByteUtils.putString;
import static com.penzzly.engine.core.mini.dsm.DynamicServerManager.*;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.lang.Thread.setDefaultUncaughtExceptionHandler;
import static java.util.UUID.fromString;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.bukkit.Bukkit.shutdown;

public class Client extends JavaPlugin {
	public static final SocketAddress MATCHMAKER = new InetSocketAddress(12345);
	public static final String FORMAT_ERR = "Thread: %sException: %s";
	private final DatagramConnection connection;
	private UUID id;
	
	public Client() throws IOException {
		connection = new DatagramConnection(getServer().getPort(), 512);
		
		connection.respond(OP_SHUTDOWN, request -> {
			if (request.get() == 1)
				getRuntime().halt(0); //Won't trigger shutdown hooks be careful.
			else
				shutdown();
			return response -> {
			};
		});
		
		setDefaultUncaughtExceptionHandler((thread, exception) -> {
			StringWriter writer = new StringWriter();
			exception.printStackTrace(new PrintWriter(writer));
			connection.send(MATCHMAKER, OP_ERROR, request ->
					putString(request, format(FORMAT_ERR, thread.toString(), writer.toString()))
			);
			
			//Rethrow
			if (exception instanceof RuntimeException)
				throw ((RuntimeException) exception);
			throw new RuntimeException(exception);
		});
	}
	
	@Override
	public void onEnable() {
		id = Single.<UUID>create(observer ->
				connection.request(OP_ONLINE, MATCHMAKER, request -> response ->
						observer.onSuccess(fromString(getString(request)))
				)
		).timeout(10, SECONDS).blockingGet();
	}
}