package com.ofs.training.java.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("DemoServlet")
public class DemoServlet extends HttpServlet {

    @Override
    public void service (HttpServletRequest req, HttpServletResponse res) throws IOException {

        res.setContentType("text/html");

        PrintWriter writer = res.getWriter();

        writer.println("hi! this is ram");
    }
}
