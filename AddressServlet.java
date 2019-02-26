package com.ofs.training.java.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ofs.training.java.pojo.PojoServiceAddress;
import com.zaxxer.hikari.HikariDataSource;

import connection.ConnectionManager;
import helper.files.JsonConverter;
import webservice2.ServiceAddress;

public class AddressServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        PojoServiceAddress pojoAddress = new PojoServiceAddress();

        HikariDataSource ds = ConnectionManager.hikariConnection();
        Connection connection = null;
        try {
            connection = ds.getConnection();
            Enumeration<String> parameterNames = req.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String s = parameterNames.nextElement();
                ServiceAddress service = new ServiceAddress();
                if (s.equals("id")) {
                    long id = Long.parseLong(req.getParameter("id"));
                    pojoAddress.setId(id);
                    PojoServiceAddress pojoAddress1 = new PojoServiceAddress();

                    pojoAddress1 = service.read(connection, pojoAddress);
                    out.println(JsonConverter.toJson(pojoAddress1));
                } else {
                    out.println(service.readAll(connection));
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        HikariDataSource ds = ConnectionManager.hikariConnection();
        Connection connection = null;
        try {
            connection = ds.getConnection();
            BufferedReader reader = req.getReader();
            List<String> jsonLines = reader.lines().collect(Collectors.toList());
            String addressJson = String.join("", jsonLines);
            System.out.format("Input JSON >> %s", addressJson);

            PojoServiceAddress input = JsonConverter.toObject(addressJson, PojoServiceAddress.class);
            PojoServiceAddress  pojoAddress = new ServiceAddress().create(connection, input);
            out.println(JsonConverter.toJson(pojoAddress));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws  IOException {

        res.setContentType("application/json");
        PrintWriter writer = res.getWriter();
        HikariDataSource ds = ConnectionManager.hikariConnection();
        Connection connection = null;
        try {
            connection = ds.getConnection();
            ServiceAddress service = new ServiceAddress();
            String path = req.getServletPath();

            if (path.equalsIgnoreCase("/address/delete")) {

                long id = Long.parseLong(req.getParameter("id"));
                PojoServiceAddress pojoAddress = new PojoServiceAddress();
                System.out.println(pojoAddress.getCity());
                pojoAddress.setId(id);
                int value = service.delete(connection, pojoAddress);
                writer.println(JsonConverter.toJson(value));
            } else if(path.equalsIgnoreCase("/address/update")) {

                BufferedReader reader = req.getReader();
                List<String> jsonLines = reader.lines().collect(Collectors.toList());
                String addressJson = String.join("", jsonLines);
                PojoServiceAddress pojoAddress = JsonConverter.toObject(addressJson, PojoServiceAddress.class);
                int  updated= service.update(connection, pojoAddress);
                writer.println(JsonConverter.toJson(updated));
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
