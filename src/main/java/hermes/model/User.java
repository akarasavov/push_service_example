package hermes.model;

import java.util.Objects;
import java.util.UUID;

public class User {

    public final UUID userId;
    public final DeviceBrand deviceBrand;

    public User(UUID userId, DeviceBrand deviceBrand) {
        this.userId = userId;
        this.deviceBrand = deviceBrand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return userId.toString();
    }
}
