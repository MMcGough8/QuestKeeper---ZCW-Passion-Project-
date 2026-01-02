package com.questkeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a location in the game world.
 * 
 * Locations are connected via exits and can contain NPCs and items.
 * Uses ID references for YAML serialization compatibility.
 * 
 * @author Marc McGough
 * @version 1.0
 */
public class Location {

    private static final String FLAG_Visited = "visited";
    private static final String FLAG_UNLOCKED = "unlocked";

    private final String id;
    private String name;
    private String description;
    private String readAloudText;

    private final Map<String, String> exits;
    private final List<String> npcs;
    private final List<String> items; 
    private final Set<String> flags;

    public Location(String id, String name) {
        this(id, name, "", "");
    }

    public Location(String id, String name, String description, String readAloudText) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Location ID cannot be null or empty");
        }
    }