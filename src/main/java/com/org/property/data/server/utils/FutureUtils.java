package com.org.property.data.server.utils;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public class FutureUtils {
	private FutureUtils()
	{}

	public static <T> CompletableFuture<T> toCompletable(Future<T> future, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return future.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		}, executor);
	}
}
