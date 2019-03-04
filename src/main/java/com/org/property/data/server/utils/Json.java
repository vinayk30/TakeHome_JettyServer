package com.org.property.data.server.utils;

import java.io.IOException;

import javax.servlet.ServletInputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;


public class Json {
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static final JsonNode MISSING_NODE = MissingNode.getInstance();

	public static JsonNode parse(ServletInputStream inputStream) {
		try {
			return mapper.readTree(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MissingNode.getInstance();
	}

	public static JsonNode parse(String requestBody) {
		try {
			return mapper.readTree(requestBody);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return MissingNode.getInstance();
	}
}
