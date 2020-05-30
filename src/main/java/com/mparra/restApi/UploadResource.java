package com.mparra.restApi;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import org.apache.commons.codec.binary.Base64;

public class UploadResource {

    // Uploading some binary file to http server
	static String PAT = "oj4yxcuj2znmdv4ky754ciiipu5bv6arvqvh3kklatvpvvekvcjq";
	
    public static void main(String[] args) {
        uploadFile(new File("C:\\Users\\marco.parra\\Pictures\\Screenshots\\new.png"));
    }

    static void uploadFile(File file) {

        if ( !file.exists()) return;

        String serverUrl = "https://dev.azure.com/marcoparra0034/_apis/wit/attachments?fileName=imageAsFileAttachment.png&api-version=5.1";
        String charset = "UTF-8";
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";
        HttpURLConnection con = null;
        try {
        	String AuthStr = ":" + PAT;
        	Base64 base64 = new Base64();

			String encodedPAT = new String(base64.encode(AuthStr.getBytes()));
        	
            // Establishing connection with server
        	URL url = new URL("https://dev.azure.com/marcoparra0034/_apis/wit/attachments?fileName=imageAsFileAttachment.png&api-version=5.1");
        	con = (HttpURLConnection) url.openConnection();
        	con.setRequestProperty("Authorization", "Basic " + encodedPAT);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (
                // Opening output stream with server
                OutputStream outputStream = con.getOutputStream();
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, charset), true) )
            {

                // Writing header data to server
                printWriter.append("--")
                        .append(boundary)
                        .append(CRLF);

                printWriter.append("Content-Disposition: form-data; name=\"fileIndexName\"; filename=\"")
                        .append(file.getName())
                        .append("\"")
                        .append(CRLF);

                printWriter.append("Content-Type: ")
                        .append(URLConnection.guessContentTypeFromName(file.getName()))
                        .append(CRLF);

                printWriter.append("Content-Transfer-Encoding: binary")
                        .append(CRLF);

                printWriter.append(CRLF).flush();

                // Writing binary data to server output stream
                Files.copy(file.toPath(), outputStream);

                outputStream.flush();

                printWriter.append(CRLF).flush();

                printWriter.append("--")
                        .append(boundary)
                        .append("--")
                        .append(CRLF)
                        .flush();

                // Server http response code
                int responseCode = ((HttpURLConnection) con).getResponseCode();

                // Buffering response body
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader((con.getInputStream())));
                StringBuilder responseBody = new StringBuilder();
                String responseBodyLine;
                while ((responseBodyLine = bufferedReader.readLine()) != null) {
                    responseBody.append(responseBodyLine);
                }

                System.out.println("Server returned http status "
                        + responseCode
                        + " from url "
                        + serverUrl
                        + " with response body "
                        + responseBody.toString());

            }

        } catch (Exception ex) {
            // Http Status >= 500 got here
            ex.printStackTrace();
        }
    }
}
