package it.hysen.vertx.websockets.demo.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import it.hysen.vertx.websockets.demo.repository.AuctionRepository;
import it.hysen.vertx.websockets.demo.validator.AuctionValidator;
import it.hysen.vertx.websockets.demo.web.AuctionHandler;

public class AuctionServiceVerticle extends AbstractVerticle {
	
	public static final Logger _logger = LoggerFactory.getLogger(AuctionServiceVerticle.class);

	private Router auctionApiRouter() {
		final AuctionRepository repository = new AuctionRepository(vertx.sharedData());
		final AuctionValidator validator = new AuctionValidator(repository);
		final AuctionHandler handler = new AuctionHandler(repository, validator);
		
		final Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		
		router.route().consumes("application/json");
		router.route().produces("application/json");

		router.route("/auctions/:id").handler(handler::initAuctionInSharedData);
		router.get("/auctions/:id").handler(handler::handleGetAuction);
		router.patch("/auctions/:id").handler(handler::handleChangeAuctionPrice);
		
		return router;
	}

	private ErrorHandler errorHandler() {
		return ErrorHandler.create();
	}
	
	private SockJSHandler eventBusHandler() {
		final BridgeOptions options = new BridgeOptions().addOutboundPermitted(new PermittedOptions().setAddressRegex("auction\\.[0-9]+"));
		return SockJSHandler.create(vertx).bridge(options, event -> {
			final BridgeEventType type = event.type();
			_logger.info("EventType: " + type);
			if (type.equals(BridgeEventType.SOCKET_CREATED)) {
				_logger.info("A socket was created");
			}
			else if (type.equals(BridgeEventType.RECEIVE)) {
				_logger.debug(event.getRawMessage());
			}
			event.complete(true);
		});
	}

	@Override
	public void start(Future<Void> startFuture) {
		final Router router = Router.router(vertx);
		
		router.route("/eventbus/*").handler(eventBusHandler());
		router.mountSubRouter("/api", auctionApiRouter());
		router.route().failureHandler(errorHandler());
		router.route().handler(staticHandler());
		
		vertx.createHttpServer().requestHandler(router::accept).listen(8080);

		startFuture.complete();
	}

	private StaticHandler staticHandler() {
		return StaticHandler.create().setCachingEnabled(false);
	}
	
}
