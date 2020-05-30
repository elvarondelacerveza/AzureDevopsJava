import java.util.Base64;

import org.apache.http.client.HttpClient;

public class CreateBug {

	public static void main(String[] args) {
		String _personalAccessToken = "oj4yxcuj2znmdv4ky754ciiipu5bv6arvqvh3kklatvpvvekvcjq";
//		   string _credentials = Convert.ToBase64String(System.Text.ASCIIEncoding.ASCII.GetBytes(string.Format("{0}:{1}", "", _personalAccessToken)));
		byte[] encodedBytes = Base64.getEncoder().encode(_personalAccessToken.getBytes());
		String _credentials = new String(encodedBytes);
		HttpClient  client;
		
		Object[] patchDocument = new Object[4];
		patchDocument[0] = new document("add","/fields/System.Title","Authorization Errors");
		patchDocument[1] = new document("add","/fields/Microsoft.VSTS.TCM.ReproSteps","Our authorization logic needs to allow for users with Microsoft accounts (formerly Live Ids) - https://msdn.microsoft.com/library/live/hh826547.aspx");
		patchDocument[2] = new document("add","/fields/Microsoft.VSTS.Common.Priority","1");
		patchDocument[3] = new document("add","/fields/Microsoft.VSTS.Common.Severity","2 - High");
		
		try{
		
			//set our headers
			
		}catch(Exception e) {
	      System.out.println(e.getMessage());
		}
		
		System.out.println();
	}
}


//	   //use the httpclient
//	   using (var client = new HttpClient())
//	   {
//	       //set our headers
//	       client.DefaultRequestHeaders.Accept.Clear();
//	       client.DefaultRequestHeaders.Accept.Add(new System.Net.Http.Headers.MediaTypeWithQualityHeaderValue("application/json"));
//	       client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", _credentials);
//
//	       //serialize the fields array into a json string
//	       var patchValue = new StringContent(JsonConvert.SerializeObject(patchDocument), Encoding.UTF8, "application/json-patch+json"); 
//
//	       var method = new HttpMethod("PATCH");
//	       var request = new HttpRequestMessage(method, "https://accountname.visualstudio.com/fabrikam/_apis/wit/workitems/$Bug?api-version=2.2") { Content = patchValue };
//	       var response = client.SendAsync(request).Result;
//
//	       //if the response is successful, set the result to the workitem object
//	       if (response.IsSuccessStatusCode)
//	       {
//	           var result = response.Content.ReadAsStringAsync().Result;
//	       }
//	   }
//	}