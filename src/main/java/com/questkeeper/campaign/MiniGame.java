package com.questkeeper.campaign;

import com.questkeeper.character.Character;
import com.questkeeper.character.Character.Skill;
import com.questkeeper.core.Dice;

import java.util.Objects;

/**
 * Represents a mini-game or puzzle within a trial.
 *
 * Mini-games are individual challenges that must be completed as part
 * of a trial. Each has a type that determines how it's solved, and can
 * be evaluated using D&D skill checks against a difficulty class (DC).
 *
 * @author Marc McGough
 * @version 1.1
 */
public class MiniGame {

    /**
     * Types of mini-games available in trials.
     */
    public enum Type {
        SEARCH("Search", "Find hidden items or clues"),
        EXAMINE("Examine", "Investigate an object closely"),
        DECODE("Decode", "Decipher a message or puzzle"),
        ALIGNMENT("Alignment", "Arrange objects in the correct order"),
        TIMING("Timing", "Complete an action with precise timing"),
        DIALOGUE("Dialogue", "Navigate a conversation"),
        MECHANISM("Mechanism", "Operate a mechanical device"),
        CHOICE("Choice", "Make a significant decision"),
        COMBAT("Combat", "Defeat enemies"),
        SKILL_CHECK("Skill Check", "Pass an ability check");

        private final String displayName;
        private final String description;

        Type(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

    private final String id;
    private String name;
    private String description;
    private String hint;
    private Type type;

    private Skill requiredSkill;
    private Skill alternateSkill;
    private int dc;
    private String reward;
    private String failConsequence;

    private boolean completed;
    private String completionText;
    private String failureText;

    /**
     * Creates a new mini-game.
     */
    public MiniGame(String id, String name, Type type) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("MiniGame ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("MiniGame name cannot be null or empty");
        }

        this.id = id.trim();
        this.name = name.trim();
        this.type = type != null ? type : Type.SKILL_CHECK;
        this.description = "";
        this.hint = "";
        this.requiredSkill = null;
        this.alternateSkill = null;
        this.dc = 10; // Default DC (Easy)
        this.reward = "";
        this.failConsequence = "";
        this.completed = false;
        this.completionText = "Challenge completed!";
        this.failureText = "You failed the challenge.";
    }

    /**
     * Creates a new mini-game with description.
     */
    public MiniGame(String id, String name, Type type, String description) {
        this(id, name, type);
        setDescription(description);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHint() {
        return hint;
    }

    public Type getType() {
        return type;
    }

    public Skill getRequiredSkill() {
        return requiredSkill;
    }

    public Skill getAlternateSkill() {
        return alternateSkill;
    }

    public int getDc() {
        return dc;
    }

    public String getReward() {
        return reward;
    }

    public String getFailConsequence() {
        return failConsequence;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getCompletionText() {
        return completionText;
    }

    public String getFailureText() {
        return failureText;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public void setHint(String hint) {
        this.hint = hint != null ? hint : "";
    }

    public void setType(Type type) {
        this.type = type != null ? type : Type.SKILL_CHECK;
    }

    public void setRequiredSkill(Skill skill) {
        this.requiredSkill = skill;
    }

    public void setAlternateSkill(Skill skill) {
        this.alternateSkill = skill;
    }

    public void setDc(int dc) {
        this.dc = Math.max(1, dc); // DC must be at least 1
    }

    public void setReward(String reward) {
        this.reward = reward != null ? reward : "";
    }

    public void setFailConsequence(String failConsequence) {
        this.failConsequence = failConsequence != null ? failConsequence : "";
    }

    public void setCompletionText(String completionText) {
        this.completionText = completionText != null ? completionText : "Challenge completed!";
    }

    public void setFailureText(String failureText) {
        this.failureText = failureText != null ? failureText : "You failed the challenge.";
    }

    // ==========================================
    // Game Actions
    // ==========================================

    /**
     * Marks the mini-game as completed.
     *
     * @return the completion text
     */
    public String complete() {
        this.completed = true;
        return completionText;
    }

    /**
     * Marks the mini-game as failed (not completed).
     *
     * @return the failure text
     */
    public String fail() {
        this.completed = false;
        return failureText;
    }

    /**
     * Resets the mini-game to its initial state.
     */
    public void reset() {
        this.completed = false;
    }


    /**
     * Evaluates the mini-game using a D&D skill check.
     *
     * The character attempts the challenge using the specified skill (or ability).
     * The approach must match either the required skill or the alternate skill.
     * A d20 is rolled, the character's skill modifier is added, and the result
     * is compared against the difficulty class (DC).
     */
    public EvaluationResult evaluate(Character character, String approach) {
        if (character == null) {
            throw new IllegalArgumentException("Character cannot be null");
        }
        if (approach == null || approach.trim().isEmpty()) {
            throw new IllegalArgumentException("Approach cannot be null or empty");
        }
        if (requiredSkill == null) {
            throw new IllegalStateException("Mini-game has no required skill set");
        }

        // Parse the approach to a Skill enum
        Skill usedSkill;
        try {
            usedSkill = Skill.valueOf(approach.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid skill: " + approach);
        }

        // Validate the skill is allowed for this mini-game
        if (!usedSkill.equals(requiredSkill) && !usedSkill.equals(alternateSkill)) {
            throw new IllegalArgumentException(String.format(
                    "Skill %s is not valid for this challenge. Use %s%s",
                    usedSkill.getDisplayName(),
                    requiredSkill.getDisplayName(),
                    alternateSkill != null ? " or " + alternateSkill.getDisplayName() : ""));
        }

        // Perform the skill check using Character's existing D&D mechanics
        int roll = Dice.rollD20();
        int modifier = character.getSkillModifier(usedSkill);
        int total = roll + modifier;
        boolean success = total >= dc;

        // Build the roll description
        String rollDescription = String.format("d20(%d) + %s(%s%d) = %d vs DC %d",
                roll,
                usedSkill.getDisplayName(),
                modifier >= 0 ? "+" : "",
                modifier,
                total,
                dc);

        // Check for natural 20 or natural 1
        boolean naturalTwenty = roll == 20;
        boolean naturalOne = roll == 1;

        // Update completion state
        if (success) {
            this.completed = true;
        }

        return new EvaluationResult(
                success,
                success ? completionText : failureText,
                success ? reward : null,
                success ? null : failConsequence,
                rollDescription,
                roll,
                total,
                naturalTwenty,
                naturalOne
        );
    }

    /**
     * Checks if a skill is valid for this mini-game.
     */
    public boolean isValidApproach(Skill skill) {
        if (requiredSkill == null) {
            return false;
        }
        return skill.equals(requiredSkill) || skill.equals(alternateSkill);
    }

    /**
     * Checks if this mini-game has an alternate skill option.
     */
    public boolean hasAlternateApproach() {
        return alternateSkill != null;
    }

    /**
     * Result of evaluating a mini-game skill check.
     */
    public record EvaluationResult(
            boolean success,
            String message,
            String reward,
            String consequence,
            String rollDescription,
            int naturalRoll,
            int totalRoll,
            boolean wasNatural20,
            boolean wasNatural1
    ) {
        /**
         * Checks if there is a reward to give.
         */
        public boolean hasReward() {
            return reward != null && !reward.isEmpty();
        }

        /**
         * Checks if there is a consequence to apply.
         */
        public boolean hasConsequence() {
            return consequence != null && !consequence.isEmpty();
        }

        /**
         * Gets a formatted result message suitable for display.
         */
        public String getFormattedResult() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== SKILL CHECK ===\n");
            sb.append(rollDescription).append("\n");

            if (wasNatural20) {
                sb.append("*** NATURAL 20! ***\n");
            } else if (wasNatural1) {
                sb.append("*** NATURAL 1! ***\n");
            }

            sb.append(success ? "SUCCESS!" : "FAILURE").append("\n\n");
            sb.append(message).append("\n");

            if (hasReward()) {
                sb.append("\nYou received: ").append(reward);
            }
            if (hasConsequence()) {
                sb.append("\nConsequence: ").append(consequence);
            }

            return sb.toString();
        }
    }

    /**
     * Gets a formatted display string for this mini-game.
     */
    public String getDisplayText() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("[%s] %s%n", type.getDisplayName(), name));
        if (!description.isEmpty()) {
            sb.append(description).append("\n");
        }
        sb.append(String.format("Status: %s%n", completed ? "COMPLETE" : "Incomplete"));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiniGame miniGame = (MiniGame) o;
        return Objects.equals(id, miniGame.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("MiniGame{id='%s', name='%s', type=%s, completed=%s}",
                id, name, type, completed);
    }
}
