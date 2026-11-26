package io.github.tomasevicjovan.qacommercelab.api.clients;

import io.github.tomasevicjovan.qacommercelab.api.model.NewCard;
import io.github.tomasevicjovan.qacommercelab.api.model.SavedCard;
import io.restassured.common.mapper.TypeRef;

import java.util.List;

import static io.restassured.http.Method.GET;
import static io.restassured.http.Method.POST;

/** The signed-in customer's saved cards. */
public final class CardsClient extends BaseClient {

    public CardsClient(ApiSession session) {
        super(session);
    }

    public List<SavedCard> list() {
        return send(GET, "/cards", request(), new TypeRef<List<SavedCard>>() { }, 200);
    }

    public SavedCard save(NewCard card) {
        return send(POST, "/cards", request().body(card), SavedCard.class, 201, 200);
    }
}
