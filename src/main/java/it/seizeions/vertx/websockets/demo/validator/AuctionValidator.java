package it.seizeions.vertx.websockets.demo.validator;

import it.seizeions.vertx.websockets.demo.entity.Auction;
import it.seizeions.vertx.websockets.demo.repository.AuctionRepository;
import it.seizeions.vertx.websockets.demo.repository.exceptions.AuctionNotFoundException;

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
