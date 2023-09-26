package com.merqury.agpu.general;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class Controllers {
    public static void sendError(int status, String message, HttpServletResponse response) throws IOException {
        response.setStatus(400);
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().print("{\"status\":%s,\"error\":\"%s\"}".formatted(status, message));
        response.getOutputStream().flush();
    }
}
