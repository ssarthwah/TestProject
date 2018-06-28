package weatherProject.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Component(immediate = true, metatype = true, label = "Get Weather Servlet Servlet")
@Service
@Properties({
	@Property(name = "sling.servlet.paths", value = "/bin/servlet/getweatherservlet"),
	@Property(name = "sling.servlet.methods", value = "GET")
})
public class GetWeatherServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(GetWeatherServlet.class);


	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

		String location= "Amsterdam" ; //default location
		 location= request.getParameter("location");
		String WEATHER_API_PATH = "http://openweathermap.org/data/2.5/weather?q="+location+"&appid=b6907d289e10d714a6e88b30761fae22";

		try
		{   
			//Create an HTTPClient object
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet getRequest = new HttpGet(WEATHER_API_PATH);
			getRequest.addHeader("accept", "application/json");

			// Read the response body
			HttpResponse httpResponse = httpClient.execute(getRequest);

			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ httpResponse.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((httpResponse.getEntity().getContent())));

			String output;
			String myJSON="" ;
			while ((output = br.readLine()) != null) {
				myJSON = myJSON + output;
			}
			
			//Create a new Gson object
			Gson gson = new Gson();

			//convert the json to  Java object (MainWeather)
			MainWeather mainWeather = gson.fromJson(br, MainWeather.class);

			WeatherBean weatherBean = new WeatherBean();
			//Set values in Bean to store the values
			setResponse(mainWeather, weatherBean);

			response.getWriter().write(JSONObject.valueToString(myJSON));
			response.setStatus(200);
			response.setContentType("application/json; charset=UTF-8");
			response.flushBuffer();    
			httpClient.getConnectionManager().shutdown();           
		}

		catch (Exception e)
		{
			logger.error("Exception***  "+e.getMessage());
			e.printStackTrace() ;
		}

	}
	//Set the json response from the service in bean     
	private void setResponse(MainWeather mainWeather, WeatherBean weatherBean) {

		logger.info(mainWeather.getName());
		logger.info(mainWeather.getMain().getTemp().toString());
		logger.info(mainWeather.getWeather().get(0).getDescription());
		logger.info(mainWeather.getMain().getHumidity().toString());

		//Set Bean to store the values
		weatherBean.setLocation(mainWeather.getName());
		weatherBean.setTemparature(mainWeather.getMain().getTemp());
		weatherBean.setDescription(mainWeather.getWeather().get(0).getDescription());
		weatherBean.setHumidity(mainWeather.getMain().getHumidity());

	}


}
