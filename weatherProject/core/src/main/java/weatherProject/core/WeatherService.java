package weatherProject.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jcr.Node;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.google.gson.Gson;


public class WeatherService extends WCMUsePojo
{

	/** The WeatherBean bean that stores values returned by the RestFul Web Service. */
	/**
	 * 
	 */
	private WeatherBean weatherBean = null;

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());


	@Override
	public void activate() throws Exception {
		
		Node currentNode = getResource().adaptTo(Node.class);

		weatherBean = new WeatherBean();  

		String location = "Amsterdam" ; //default location

		//Get authored location Value from the component node 
		if(currentNode.hasProperty("location")){
			location = currentNode.getProperty("./location").getString();
		}  
		log.info("****  location IS "+ location );

		//Get Restful Web Service Data
		BufferedReader br =getJSON(location)   ;          

		//Create a new Gson object
        Gson gson = new Gson();

        //convert the JSON to  Java object (MainWeather)
        MainWeather mainWeather = gson.fromJson(br, MainWeather.class);	
		
		//Set values in Bean to store the values
		setResponse(mainWeather, weatherBean);

	}


	//Set the json response from the service in bean     
	private void setResponse(MainWeather mainWeather, WeatherBean weatherBean) {

		log.info(mainWeather.getName());
		log.info(mainWeather.getMain().getTemp().toString());
		log.info(mainWeather.getWeather().get(0).getDescription());
		log.info(mainWeather.getMain().getHumidity().toString());

		//Set Bean to store the values
		weatherBean.setLocation(mainWeather.getName().toUpperCase());
		weatherBean.setTemparature(mainWeather.getMain().getTemp());
		weatherBean.setDescription(mainWeather.getWeather().get(0).getDescription().toUpperCase());
		weatherBean.setHumidity(mainWeather.getMain().getHumidity());

	}


	public WeatherBean getWeatherBean() {
		return this.weatherBean;
	}


	//Invokes a third party Restful Web Service and returns the results in a JSON String
	private static BufferedReader getJSON(String location)
	{

		try
		{
			//Create an HTTPClient object
			DefaultHttpClient httpClient = new DefaultHttpClient();

			//create the api url using configured location
			String weatherAPI = "http://openweathermap.org/data/2.5/weather?q="+location+"&appid=b6907d289e10d714a6e88b30761fae22";

			HttpGet getRequest = new HttpGet(weatherAPI);
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			// Read the response body
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			httpClient.getConnectionManager().shutdown();
			return br ;
		}

		catch (Exception e)
		{
			e.printStackTrace() ;
		}
		return null;
	}


}