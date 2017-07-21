package it.hysen.vertx.websockets.demo.repository;

import java.math.BigDecimal;
import java.util.Optional;

import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import it.hysen.vertx.websockets.demo.entity.Auction;

public class AuctionRepository {
	
	private final SharedData sharedData;
	
	public AuctionRepository(SharedData sharedData) {
		this.sharedData = sharedData;
	}
	
	private Auction convertToAuction(LocalMap<String, String> auction) {
		return new Auction(auction.get("id"), new BigDecimal(auction.get("price")));
	}
	
	public Optional<Auction> getById(String auctionId) {
		final LocalMap<String, String> auctionSharedData = sharedData.getLocalMap(auctionId);
		return Optional.of(auctionSharedData).filter(m -> !m.isEmpty()).map(this::convertToAuction);
	}
	
	public void save(Auction auction) {
		final LocalMap<String, String> auctionSharedData = sharedData.getLocalMap(auction.getId());
		
		auctionSharedData.put("id", auction.getId());
		auctionSharedData.put("price", auction.getPrice().toString());
	}
	
}
