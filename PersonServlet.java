package com.ofs.training.java.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.ofs.training.java.pojo.PojoServicePerson;
import com.zaxxer.hikari.HikariDataSource;

import connection.ConnectionManager;
import exception.AppException;
import helper.files.JsonConverter;
import webservice2.ServicePerson;

public class PersonServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        PojoServicePerson pojoPerson = new PojoServicePerson();

        HikariDataSource ds = ConnectionManager.hikariConnection();
        Connection connection = null;
        try {
            connection = ds.getConnection();
            Enumeration<String> parameterNames = req.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String s = parameterNames.nextElement();
                ServicePerson service = new ServicePerson();
                if (s.equals("id")) {
                    long id = Long.parseLong(req.getParameter("id"));
                    pojoPerson.setId(id);

                    pojoPerson = service.read(connection, pojoPerson, true);
                    out.println(JsonConverter.toJson(pojoPerson));
                } else {
                    res.setStatus(HttpStatus.SC_OK);
                    List<PojoServicePerson> personservicepojo = service.readAll(connection);
                    out.println(JsonConverter.toJson(personservicepojo));
                }
            }
            connection.close();
        } catch (AppException e) {
            e.printStackTrace();
            out.write(JsonConverter.toJson(e.getErrorCodes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("application/json");

        PrintWriter out = res.getWriter();
        ServicePerson service = new ServicePerson();
        PojoServicePerson pojoPerson = new PojoServicePerson();
        HikariDataSource ds = ConnectionManager.hikariConnection();

        Connection connection = null;
        try {
            connection = ds.getConnection();
            String path = req.getServletPath();

            if (path.equalsIgnoreCase("/person/delete")) {
                long id = Long.parseLong(req.getParameter("id"));
                pojoPerson.setId(id);
                int isDeleted = service.delete(connection, pojoPerson);
                out.println(JsonConverter.toJson(isDeleted));
            } else if(path.equalsIgnoreCase("/person/update")) {

                BufferedReader reader = req.getReader();
                List<String> jsonLines = reader.lines().collect(Collectors.toList());
                String personJson = String.join("", jsonLines);
                System.out.format("Input JSON >> %s", personJson);
                PojoServicePerson Person = JsonConverter.toObject(personJson, PojoServicePerson.class);
                int updated = service.update(connection, Person);
                System.out.println(updated);
                out.println(JsonConverter.toJson(updated));
            }
            connection.close();

        } catch (AppException e) {
            e.printStackTrace();
            res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            out.write(JsonConverter.toJson(e.getErrorCodes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws  IOException {

        res.setContentType("application/json");
        PrintWriter out = res.getWriter();
        ServicePerson service = new ServicePerson();
        HikariDataSource ds = ConnectionManager.hikariConnection();
        Connection connection = null;
        try {
            connection = ds.getConnection();
            BufferedReader reader = req.getReader();
            List<String> jsonLines = reader.lines().collect(Collectors.toList());
            String personJson = String.join("", jsonLines);
            System.out.format("Input JSON >> %s", personJson);

            System.out.println("input");
            PojoServicePerson input = JsonConverter.toObject(personJson, PojoServicePerson.class);
            PojoServicePerson  Person = service.create(connection, input);
            out.println(JsonConverter.toJson(Person));
        } catch (AppException e) {
            e.printStackTrace();
            res.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            out.write(JsonConverter.toJson(e.getErrorCodes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


