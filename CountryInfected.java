public class CountryInfected {
	private String country;
	private int[] infected;
	
	// Constructor	
	public CountryInfected(String country, int[] infected) {
		this.country = country;
		this.infected = infected;
	}

	// Getter
	public int[] getInfected() {
		return this.infected;
	}

	// Getter
	public String getCountry() {
		return this.country;
	}
}
