package org.cubegame.domain.model.message;

import org.cubegame.domain.model.identifier.UserId;

public class Author {
    private final UserId userId;
    private final String firstName;

    public Author(final UserId userId, final String firstName) {
        this.userId = userId;
        this.firstName = firstName;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }
}
