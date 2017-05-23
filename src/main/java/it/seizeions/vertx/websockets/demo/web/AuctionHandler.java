package it.seizeions.vertx.websockets.demo.web;

import java.math.BigDecimal;
import java.util.Optional;

import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import it.seizeions.vertx.websockets.demo.entity.Auction;
import it.seizeions.vertx.websockets.demo.repository.AuctionRepository;
import it.seizeions.vertx.websockets.demo.validator.AuctionValidator;

public class AuctionHandler {
	
	public static final Logger _logger = LoggerFactory.getLogger(AuctionHandler.class);

	private final AuctionRepository repository;
	private final AuctionValidator validator;
	
	public AuctionHandler(AuctionRepository repository, AuctionValidator validator) {
		this.repository = repository;
		this.validator = validator;
	}
	
	public void handleChangeAuctionPrice(RoutingContext context) {
		final String auctionId = context.request().getParam("id");
		final Auction auctionRequest = new Auction(auctionId, new BigDecimal(context.getBodyAsJson().getString("price")));
		
		_logger.info(auctionRequest);

		if (validator.validate(auctionRequest)) {
			repository.save(auctionRequest);
			context.vertx().eventBus().publish("auction." + auctionId, context.getBodyAsString());
			
			context.response().setStatusCode(200).end();
		}
		else {
			context.response().setStatusCode(422).end();
		}
	}
	
	public void handleGetAuction(RoutingContext context) {
		final String auctionId = context.request().getParam("id");
		final Optional<Auction> auction = repository.getById(auctionId);
		
		if (auction.isPresent()) {
			context.response().putHeader("content-type", "application/json").setStatusCode(200).end(Json.encodePrettily(auction.get()));
		}
		else {
			context.response().putHeader("content-type", "application/json").setStatusCode(404).end();
		}
	}
	
	public void initAuctionInSharedData(RoutingContext context) {
		final String auctionId = context.request().getParam("id");
		
		final Optional<Auction> auction = repository.getById(auctionId);
		if (!auction.isPresent()) {
			repository.save(new Auction(auctionId));
		}
		
		context.next();
	}
	
}
