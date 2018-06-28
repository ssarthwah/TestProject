package weatherProject.core;

public class WeatherBean {
	
	private String location ;
	private Integer weather ;
	private Double temparature ;
	private String description ;
	private Double humidity ;

	public Integer getWeather() {
		return weather;
	}

	public void setWeather(Integer weather) {
		this.weather = weather;
	}

	public Double getTemparature() {
		return temparature;
	}

	public void setTemparature(Double temparature) {
		this.temparature = temparature;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getHumidity() {
		return humidity;
	}

	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}


}
