package com.servlets;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

public class MainServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(MainServlet.class);

    private String filePath;

    public void init() throws ServletException {
        // Define base path.
        this.filePath = System.getProperty("user.home") + "/uploadedFiles";
        log.debug("Servlet initialized");
        // In a Windows environment with the Applicationserver running on the
        // c: volume, the above path is exactly the same as "c:\\uploadedFiles".
        // In UNIX, it is just straightforward "/uploadedFiles".
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        log.debug("Multipart = " + isMultipart);

        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();

        // Configure a repository (to ensure a secure temp location is used)
        File repository = new File(filePath);
        if (!repository.exists())
            repository.mkdir();
        log.debug("Repository = " + repository);
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
                    log.debug("fileName is: " + fileName + ", content type is: " + contentType);

                    String fileExtention = fileName.substring(fileName.lastIndexOf('.'));
                    File uploadedFile = new File(repository, newFileName + fileExtention);
                    item.write(uploadedFile);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        //Get the list of all files
        String[] files = new File(filePath).list();
        for (String tmp : files) {
            log.debug("file is: " + tmp);
        }

        req.getSession().setAttribute("IMAGES_LIST", files);
        resp.sendRedirect("listImages.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestedFile = filePath + req.getPathInfo();
        log.debug("Requested file = " + requestedFile);

        byte[] image = Files.readAllBytes(Paths.get(requestedFile));

        resp.setContentType("image/jpg");
        BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
        out.write(image);
        out.close();
    }
}
