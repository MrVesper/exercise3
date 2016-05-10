package wdsr.exercise3.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class StatusChecker extends RestClientBase {
	public StatusChecker(final String serverHost, final int serverPort, final Client client) {
		super(serverHost, serverPort, client);
	}
	
	public boolean isServerOk() {
		WebTarget statusTarget = baseTarget.path("/status");
		Response response = statusTarget
		        .request(MediaType.TEXT_PLAIN)
		        .get(Response.class);
		
		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			return false;
		}
		
		String status = response.readEntity(String.class);
		return "OK".equals(status);
	}
}
