package com.battleship.ships;

import com.battleship.position.PositionPair;
import com.battleship.ships.annotations.RegisterShip;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Set;

/**
 * Automatically discovers all new ship types and create necessary ship when createShip is called.
 *
 * For adding new Ship, extend AbstractShip and
 * add registerShip annotation specifying the type of the ship and it is would be automatically registered.
 *
 */
public class ShipFactory {
    private static HashMap<String, Class<Ship>> shipClasses = new HashMap<>();


    static {
        System.out.println("Discovering registered ships ..");
        Reflections reflections = new Reflections("com.battleship.ships",
                new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Class<?>> matchingClasses = reflections.getTypesAnnotatedWith(RegisterShip.class);
        System.out.println("Registered ships" + matchingClasses);
        for (Class matchingClass : matchingClasses) {
            RegisterShip registerShip = (RegisterShip) matchingClass.getAnnotation(RegisterShip.class);
            if (registerShip.type() != null && !shipClasses.containsKey(registerShip.type())) {
                shipClasses.put(registerShip.type(), matchingClass);
            } else {
                throw new IllegalArgumentException("Duplicate/invalid ship's registered");
            }


        }
        System.out.println("Discovered ships " + shipClasses);


    }

    public static Ship createShip(String shipType, PositionPair shipDimension) throws Exception {

        if (StringUtils.isEmpty(shipType) || !shipClasses.containsKey(shipType)) {
            System.err.println(String.format("Invalid Ship type provided %s", shipType));
            throw new IllegalArgumentException();
        }

        Constructor<Ship> shipConstructor = shipClasses.get(shipType).getDeclaredConstructor(PositionPair.class);
        System.out.println("Creating ship with height =" + shipDimension.getPosX().getInt() + " and width=" + shipDimension.getPosY().getInt());
        Ship ship = shipConstructor.newInstance(shipDimension);
        return ship;

    }
}
