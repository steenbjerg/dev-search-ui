package dk.stonemountain.business.ui.util.jaxrs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jakarta.json.bind.config.PropertyVisibilityStrategy;

public class PrivateVisibilityStrategy implements PropertyVisibilityStrategy {
    @Override
    public boolean isVisible(Field field) {
        return true;
    }

    @Override
    public boolean isVisible(Method method) {
        return false;
    }
}