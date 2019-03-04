package com.org.property.data.server.web;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ManagedAsync;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.org.property.data.server.utils.FutureUtils;
import com.org.property.data.server.utils.Json;

@Path("/")
@Singleton
public class Application extends javax.ws.rs.core.Application {

	private static final String TARGET_URL = "http://webservice-takehome-1.spookle.xyz/";
	private static final String PATH = "/properties";
	private static final String PROPERTY = "property";
	private static final String PROPERTY_ID = "property_id";
	private final WebTarget upstream = ClientBuilder.newClient().target(TARGET_URL);

	@Inject
	private TaskExecutor executor;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PATH)
	@ManagedAsync
	public void propertys(@Suspended final AsyncResponse asyncResponse, String requestBody) throws IOException {
		JsonNode payload = Json.parse(requestBody);

		Vector<CompletableFuture<Response>> upstreamRequests = new Vector<>();

		if (!payload.isMissingNode()) {
			for (JsonNode node : ((ArrayNode) payload)) {
				if (node.isTextual()) {
					WebTarget target = upstream.path(PROPERTY).queryParam(PROPERTY_ID, node.asText());

					upstreamRequests.add(FutureUtils.toCompletable(target.request().async().get(), executor));
				}
			}
		}

		System.out.println("Total number of properties---->" + upstreamRequests.size());
		CompletableFuture.allOf(upstreamRequests.toArray(new CompletableFuture[upstreamRequests.size()]))
				.thenRunAsync(() -> {
					asyncResponse.resume(
							upstreamRequests.stream().map(new Function<CompletableFuture<Response>, JsonNode>() {

								@Override
								public JsonNode apply(CompletableFuture<Response> responseCf) {
									try {
										return Json.parse(responseCf.get().readEntity(String.class));
									} catch (InterruptedException | ExecutionException e) {
										e.printStackTrace();
									}
									return Json.MISSING_NODE;
								}

							}).max((leftValue, rightValue) -> {
								return Integer.compare(leftValue.path("value").asInt(),
										rightValue.path("value").asInt());
							}).get().toString());
				});
	}
}
