package com.servlets;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MainServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(MainServlet.class);

    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        log.debug("Multipart is = " + isMultipart);
        log.debug("PATH = " + req.getPathInfo());

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = new File(getServletContext().getInitParameter("upload.location"));
        log.info(repository);
        factory.setRepository(repository);

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);

        // Parse the request
        String newFileName = null;
        try {
            List<FileItem> items = upload.parseRequest(req);
            log.debug("items size is " + items.size());

            // Process the uploaded items
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = iter.next();
                if (item.isFormField()) {
                    newFileName = item.getString();
                    log.debug("value " + newFileName);
                } else {
                    // Process a file upload
                    String fileName = item.getName();
                    String contentType = item.getContentType();
                    String fileExtention = fileName.substring(fileName.lastIndexOf('.'));
                    log.debug("fileName is: " + fileName + ", content type is: " + contentType);
                    File uploadedFile = new File(repository, newFileName + fileExtention);
                    item.write(uploadedFile);
                }
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
        File[] files = new File(getServletContext().getInitParameter("upload.location")).listFiles();
        for(File tmp : files) {
            log.debug("file is: " + tmp);
        }
        req.setAttribute("IMAGES_LIST", files);
        resp.sendRedirect("listImages.jsp");
    }
}
