package it.seizeions.vertx.websockets.demo.repository.exceptions;

public class AuctionNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public AuctionNotFoundException(String auctionId) {
		super("Auction not found: " + auctionId);
	}

}
