package com.mparra.restApi;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author marco.parra Conexion with AzureDevops
 */
public class ResApiMain {

	static String ServiceUrl = "https://dev.azure.com/{organization}/";
	static String TeamProjectName = "{ProjectName}";
	static String UrlEndGetWorkItemById = "/_apis/wit/workitems/";
	static String UrlEndAttachments = "/_apis/wit/attachments";
	static String ApiVersion = "?api-version=5.1";
	static Integer WorkItemId = 3;
	static String PAT = "";

	// Return HttpURLConnection from AzureDevops
	public static HttpURLConnection apiConnection(String PAT, URL url) {
		HttpURLConnection con = null;
		try {
			String AuthStr = ":" + PAT;
			Base64 base64 = new Base64();

			String encodedPAT = new String(base64.encode(AuthStr.getBytes()));
			// https://dev.azure.com/{organization}/{project}/_apis/wit/workitems/${type}?api-version=5.1
//		URL url = new URL(ServiceUrl + TeamProjectName + UrlEndGetWorkItemById + WorkItemType + ApiVersion );
			con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Authorization", "Basic " + encodedPAT);
			con.setDoOutput(true);
			System.out.println("URL - " + url.toString());
			System.out.println("PAT - " + encodedPAT);

			// Image Requierements
//			con.setRequestProperty("Content-Type", "image/jpeg");
			con.setDoInput(true);
			con.setUseCaches(false);
			con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json-patch+json; utf-8");
//		con.setRequestProperty("Accept", "application/json");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return con;
	}

	public static HttpURLConnection apiConnectionAttachments(String PAT, URL url) {
		HttpURLConnection con = null;
		try {
			String AuthStr = ":" + PAT;
			Base64 base64 = new Base64();

			String encodedPAT = new String(base64.encode(AuthStr.getBytes()));
			// https://dev.azure.com/{organization}/{project}/_apis/wit/workitems/${type}?api-version=5.1
//		URL url = new URL(ServiceUrl + TeamProjectName + UrlEndGetWorkItemById + WorkItemType + ApiVersion );
			con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("Authorization", "Basic " + encodedPAT);
			con.setDoOutput(true);
			System.out.println("URL - " + url.toString());
			System.out.println("PAT - " + encodedPAT);

			// Image Requierements
//			con.setRequestProperty("Content-Type", "image/jpeg");
			con.setDoInput(true);
			con.setUseCaches(false);
//			con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/octet-stream");
//		con.setRequestProperty("Accept", "application/json");
//      multipath
			// forname
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return con;
	}

	public static void createWorkItem(String nameWorkItem, String workItemType) {
		try {
			// https://dev.azure.com/{organization}/{project}/_apis/wit/workitems/${type}?api-version=5.1
			workItemType = workItemType.replace(" ", "%20");
			workItemType = "$" + workItemType;
			URL url = new URL(ServiceUrl + TeamProjectName + UrlEndGetWorkItemById + workItemType + ApiVersion);
			HttpURLConnection con = ResApiMain.apiConnection(PAT, url);
			String jsonInputString = "[{\"op\":\"add\",\"path\":\"/fields/System.Title\",\"value\":\"" + nameWorkItem
					+ "\"}]";

			try (OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				System.out.println("Se ha agregado con exito el" + workItemType + nameWorkItem);
				System.out.println(new String(input));
				os.write(input, 0, input.length);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				System.out.println(response.toString());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			con.disconnect();
		} catch (Exception ex) {

		}
	}

	public static void readWorkItem(Integer id) {
		try {

			URL url = new URL(ServiceUrl + TeamProjectName + UrlEndGetWorkItemById + id.toString());
			HttpURLConnection con = ResApiMain.apiConnection(PAT, url);
			con.setRequestMethod("GET");

			int status = con.getResponseCode();

			if (status == 200) {
				String responseBody;
				try (Scanner scanner = new Scanner(con.getInputStream())) {
					responseBody = scanner.useDelimiter("\\A").next();
					System.out.println(responseBody);
				}

				try {
					Object obj = new JSONParser().parse(responseBody);
					JSONObject jo = (JSONObject) obj;

					String WIID = (String) jo.get("id").toString();
					Map<String, String> fields = (Map<String, String>) jo.get("fields");
					System.out.println("WorkItemId - " + WIID);
					System.out.println("WorkItemTitle - " + fields.get("System.Title"));
					System.out.println("WorkItemProyect - " + fields.get("System.TeamProject"));
					System.out.println("WorkItemType - " + fields.get("System.WorkItemType"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			con.disconnect();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateWorkItem(Integer id) {
		// Vamos actualizar un bug cambiando titulo
		// https://dev.azure.com/{organization}/{project}/_apis/wit/workitems/{id}?api-version=5.1
		try {
			URL url = new URL(ServiceUrl + TeamProjectName + UrlEndGetWorkItemById + id.toString() + ApiVersion);
			HttpURLConnection con = ResApiMain.apiConnection(PAT, url);
			try (OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				System.out.println("Se ha actualizado con exito el");
				System.out.println(new String(input));
				os.write(input, 0, input.length);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				System.out.println(response.toString());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			con.disconnect();
		} catch (Exception ex) {

		}
	}

	private static byte[] extractBytes(String ImageName) throws IOException {
		// open image
		File imgPath = new File(ImageName);
		BufferedImage bufferedImage = ImageIO.read(imgPath);

		// get DataBufferBytes from Raster
		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

		return (data.getData());
	}

	// Attachments
	public static void putAttachments(Integer id) {
		try {
//			InputStreamReader 
//			FileInputStream 
			URL url = new URL(
			HttpURLConnection con = ResApiMain.apiConnectionAttachments(PAT, url);
			con.setRequestMethod("POST");
//			String jsonInputString = "User text content to upload";
				try (BufferedInputStream is = new BufferedInputStream(new FileInputStream("PATH MEDIA"))) {
				byte[] input = IOUtils.toByteArray(is);
				OutputStream os = con.getOutputStream();
				os.write(input, 0, input.length);
				os.close();
			}catch(Exception e) {
				e.getMessage();
			}
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				System.out.println(response.toString());
				Object obj = new JSONParser().parse(response.toString());
				JSONObject jo = (JSONObject) obj;

				String WIID = (String) jo.get("id").toString();
				String WIURL = (String) jo.get("url").toString();
				System.out.println("Attachment Id=  " + WIID);
				System.out.println("Attachment Url =  " + WIURL);
				AssignAttachment(id, WIURL);
		} catch (Exception ex) {

		}
		}catch(Exception s) {
			s.getMessage();
		}
	}
	
private static void AssignAttachment(Integer woitid , String attachmentURL) {
		// *************************** Asign Attachment to a Working Item ******************************
		//PATCH https://dev.azure.com/fabrikam/_apis/wit/workitems/{id}?api-version=5.1		
		try {
			URL url = new URL(ServiceUrl + TeamProjectName + UrlEndGetWorkItemById + woitid.toString() + ApiVersion);
			HttpURLConnection con = ResApiMain.apiConnection(PAT, url);
//		// First we need to have the URL for connecting and send by request method this 
			String jsonInputString = "[ {\r\n" + 
					"    \"op\": \"add\",\r\n" + 
					"    \"path\": \"/relations/-\",\r\n" + 
					"    \"value\": {\r\n" + 
					"      \"rel\": \"AttachedFile\",\r\n" + 
					"      \"url\": \""+attachmentURL+"\",\r\n" + 
					"      \"attributes\": {\r\n" + 
					"        \"comment\": \"Spec for the work\"\r\n" + 
					"      }\r\n" + 
					"    }\r\n" + 
					"  }]";
			try (OutputStream os = con.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				System.out.println("Se ha actualizado con exito el WorkItem Id"+ woitid);
				System.out.println(new String(input));
				os.write(input, 0, input.length);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				System.out.println(response.toString());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			con.disconnect();
		} catch (Exception ex) {

		}
	}
	
	public static void main(String[] args) {
		// Work Item Types
		/*
		 * User story Bug Epic Test Case Feature
		 */
//		createWorkItem("Check whether the user can ", "test case");
//		readWorkItem(17);
//		putAttachments(16);
		updateWorkItem(16);
//		createComments(16);
		/*
		 * Work ITEM KEYS
		 * 
		 * ['System.AuthorizedDate', 'System.WorkItemType', 'System.Title',
		 * 'System.IterationLevel1', 'System.IterationLevel2', 'System.History',
		 * 'System.AreaId', 'System.NodeName', 'Microsoft.VSTS.Common.ActivatedDate',
		 * 'System.IterationId', 'System.IterationPath', 'System.PersonId',
		 * 'System.AssignedTo', 'System.AuthorizedAs', 'System.Id', 'System.Reason',
		 * 'System.CreatedBy', 'Microsoft.VSTS.Common.StateChangeDate',
		 * 'Microsoft.VSTS.Common.Priority', 'System.Watermark',
		 * 'Microsoft.VSTS.Common.ActivatedBy', 'System.AreaPath', 'System.State',
		 * 'System.ChangedDate', 'System.AreaLevel1', 'System.CreatedDate',
		 * 'System.TeamProject', 'System.Rev', 'System.ChangedBy', 'System.RevisedDate']
		 */
	}
}
