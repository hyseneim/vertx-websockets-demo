package it.hysen.vertx.websockets.demo.validator;

import it.hysen.vertx.websockets.demo.entity.Auction;
import it.hysen.vertx.websockets.demo.repository.AuctionRepository;
import it.hysen.vertx.websockets.demo.repository.exceptions.AuctionNotFoundException;

public class AuctionValidator {
	
	private final AuctionRepository repository;

	public AuctionValidator(AuctionRepository repository) {
		this.repository = repository;
	}

	public boolean validate(Auction auction) {
		final Auction auctionDatabase = repository.getById(auction.getId()).orElseThrow(() -> new AuctionNotFoundException(auction.getId()));

		return auctionDatabase.getPrice().compareTo(auction.getPrice()) == -1;
	}

}
