package dansplugins.simpleskills.skill.abs;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.config.ConfigService;

import dansplugins.simpleskills.message.MessageService;
import dansplugins.simpleskills.logging.Log;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Callum Johnson
 * @since 05/01/2022 - 13:14
 */
public abstract class AbstractSkill implements Listener {
    protected final ConfigService configService;
    protected final Log log;
    protected final PlayerRecordRepository playerRecordRepository;
    protected final SimpleSkills simpleSkills;
    protected final MessageService messageService;

    private final String name;
    private final String benefitConfigKey;  // Cached config key for benefit checking
    private final HashMap<Class<? extends Event>, List<Method>> handlers = new HashMap<>();
    private int expReq;
    private double expFactor;
    private boolean active;

    /**
     * The overhead Skill abstraction class.
     * <p>
     * The trigger mechanism has been added to expand upon the current functionality
     * of the codebase, each trigger corresponds to a method within the extended class,
     * providing the ability to create multiple events/handlers for one skill, 'Farming'
     * would theoretically require both breaking and placing blocks for the skill to be
     * conceptually correct.<br>
     * For every '{@link Event}' specified in <em>triggers</em>, ensure a method
     * returning {@code void} exists which has {@code 1} parameter for that '{@link Event}'.
     * <br><br>
     * An example of this could be 'Farming' and {@link BlockBreakEvent} with a method defined
     * as such:
     * <br><code>
     * public void handleBlockBreak(BlockBreakEvent event) {
     * // Default Method Stub.
     * }
     * </code>
     * </p>
     * <p>
     * There is no fixed set of events a skill may listen to: any Bukkit {@link Event} class is a
     * valid trigger. A trigger is matched on the event's <em>exact</em> runtime class, though, so
     * declaring a base class does not catch its subclasses — a skill reacting to damage dealt by
     * a mob has to declare {@code EntityDamageByEntityEvent} itself rather than relying on
     * {@code EntityDamageEvent} to cover it.
     * </p>
     * @param configService
     * @param log
     * @param playerRecordRepository
     * @param simpleSkills
     * @param messageService
     * @param name     of the skill.
     * @param triggers or events linked to the skill.
     */
    @SafeVarargs
    public AbstractSkill(ConfigService configService, Log log, PlayerRecordRepository playerRecordRepository, SimpleSkills simpleSkills, MessageService messageService, @NotNull String name, @NotNull Class<? extends Event>... triggers) {
        this.configService = configService;
        this.log = log;
        this.playerRecordRepository = playerRecordRepository;
        this.simpleSkills = simpleSkills;
        this.messageService = messageService;

        if (name.isEmpty()) throw new IllegalArgumentException("Skill requires a name.");
        if (triggers.length == 0) throw new IllegalArgumentException("Skill cannot have zero triggers.");
        this.name = name;
        this.benefitConfigKey = generateBenefitConfigKey(name);
        // Read activation state from config, default to true for backward compatibility
        this.active = this.configService.getConfig().getBoolean("skills." + getName() + ".active", true);
        this.expReq = this.configService.getConfig().getInt("defaultBaseExperienceRequirement", 10);
        this.expFactor = this.configService.getConfig().getDouble("defaultExperienceIncreaseFactor", 1.2);
        setupTriggers(triggers);
    }

    /**
     * Method to scan the classes' methods for trigger-setting.
     * <p>
     * Due to the nature of the trigger->method connections formed for skills,
     * this method scans all of the methods in the class and checks them for
     * compatibility with the defined triggers, if the class matches, the method
     * is then added to the map of methods, these methods are <em>all</em> called
     * upon the trigger.
     * </p>
     *
     * @param triggers to setup.
     * @see #AbstractSkill(ConfigService, Log, PlayerRecordRepository, SimpleSkills, MessageService, String, Class[])
     */
    private void setupTriggers(@NotNull Class<? extends Event>[] triggers) {
        for (@NotNull Class<? extends Event> trigger : triggers) {
            for (@NotNull Method method : getClass().getMethods()) {
                if (!method.getReturnType().equals(Void.TYPE)) continue;
                if (method.getParameterCount() != 1) continue;
                if (!Modifier.isPublic(method.getModifiers())) continue;
                final Class<?> parameterType = method.getParameterTypes()[0];
                if (!parameterType.equals(trigger)) continue;
                final List<Method> methods = handlers.getOrDefault(trigger, new ArrayList<>());
                methods.add(method);
                handlers.put(trigger, methods);
            }
        }
    }

    /**
     * Method to encapsulate all events for triggering the Skill.
     * <p>
     * This method references {@link #handlers} which is defined by {@link #setupTriggers(Class[])},
     * each method within the map is called if the event currently being handled by this method
     * is compatible with the specific trigger. Dispatch is on the event's exact class, so an
     * event only reaches a trigger method declared for that class.
     * </p>
     * <p>
     * Delivering the same event to a skill more than once is prevented by {@link #register()},
     * which forwards each registration only the class it was made for, rather than by this
     * method remembering the events it has already seen.
     * </p>
     *
     * @param event to handle.
     */
    public void handle(Event event) {
        if (!active) return;
        if (event instanceof Cancellable) if (((Cancellable) event).isCancelled()) return;
        final List<Method> methods = handlers.getOrDefault(event.getClass(), new ArrayList<>());
        for (Method method : methods) {
            try {
                method.invoke(this, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                // The reflective wrapper hides what actually went wrong, so the underlying
                // failure is both logged and attached as the cause of the rethrown exception.
                final Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                log.error("Failed to trigger '" + name + "' with event '" + event.getEventName() +
                        "' due to " + cause);
                throw new IllegalStateException("Failed to trigger '" + name + "' with event '" +
                        event.getEventName() + "'!", cause);
            }
        }
    }

    /**
     * Method to obtain the pretty-formatted name of the Skill.
     *
     * @return name of the skill, modified with {@link WordUtils#capitalizeFully(String)}.
     */
    @NotNull
    public String getName() {
        return WordUtils.capitalizeFully(name.replaceAll("_", " ").toLowerCase());
    }

    // Getters & Setters

    public int getId() {
        return hashCode();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getExpRequirement() {
        return expReq;
    }

    public void setExpRequirement(int expReq) {
        this.expReq = expReq;
    }

    public double getExpFactor() {
        return expFactor;
    }

    public void setExpFactor(double expFactor) {
        this.expFactor = expFactor;
    }

    /**
     * Method to obtain the highest level this skill can reach.
     * <p>
     * The cap is read from the config on every call rather than cached at construction, so that
     * a value changed by {@code /ss reload} is reported without a restart. The same key backs the
     * cap {@link dansplugins.simpleskills.playerrecord.PlayerRecord#incrementExperience(int)}
     * enforces, so what is displayed and what is enforced cannot drift apart.
     * </p>
     *
     * @return the configured {@code defaultMaxLevel}, or {@code 100} if the key is absent.
     */
    public int getMaxLevel() {
        return configService.getConfig().getInt("defaultMaxLevel", 100);
    }

    // Methods

    public void incrementExperience(@NotNull Player player) {
        if (randomExpGainChance()) {
            final double randomChance = obtainRandomChance();
            if (!(randomChance <= getChance())) return;
        }
        final PlayerRecord playerRecord = getRecord(player);
        if (playerRecord == null) {
            log.error("A player record wasn't found for " + player.getName() + " while attempting to increment experience.");
            return;
        }
        final int skillId = getId();
        playerRecord.incrementExperience(skillId);
    }

    @Nullable
    public PlayerRecord getRecord(@NotNull Player player) {
        return playerRecordRepository.getPlayerRecord(player.getUniqueId());
    }

    /**
     * Generates a camelCase config key from the skill name for benefit checking.
     * <p>
     * This is a static helper method used during skill construction to generate
     * the config key once and cache it for performance.
     * </p>
     *
     * @param skillName the internal skill name
     * @return the generated config key (e.g., "boatingBenefitEnabled", "monsterHuntingBenefitEnabled")
     */
    private static String generateBenefitConfigKey(@NotNull String skillName) {
        // Convert skill name to camelCase config key (e.g., "Boating" -> "boating", "Monster Hunting" -> "monsterHunting")
        String processedName = skillName.replaceAll("_", " ");  // Handle underscore-separated names (if any)
        String[] words = processedName.split("\\s+");
        StringBuilder configKey = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i].trim().toLowerCase();
            if (word.isEmpty()) continue;  // Skip empty words from multiple spaces
            if (configKey.length() == 0) {
                // First word is lowercase
                configKey.append(word);
            } else {
                // Subsequent words are capitalized
                configKey.append(Character.toUpperCase(word.charAt(0)))
                         .append(word.substring(1));
            }
        }
        configKey.append("BenefitEnabled");
        return configKey.toString();
    }

    /**
     * Method to check if benefits are enabled for this skill.
     * <p>
     * This method checks the configuration for a skill-specific benefit toggle.
     * The config key is generated from the skill name and cached for performance.
     * </p>
     * <p>
     * If the config key is not found, it defaults to {@code true} to maintain backward compatibility
     * and ensure new skills work without manual config updates.
     * </p>
     *
     * @return {@code true} if benefits are enabled for this skill, {@code false} otherwise.
     */
    protected boolean isBenefitEnabled() {
        return configService.getConfig().getBoolean(benefitConfigKey, true);
    }

    public double obtainRandomChance() {
        return Math.random() * 100;
    }

    /**
     * Method to dynamically register a listener for every event this skill declares a trigger for.
     * <p>
     * This enables the "Skill" class to listen to multiple events for the
     * listener-trigger-hook system.
     * </p>
     * <p>
     * Only the classes in {@link #handlers} are registered: an event no trigger method accepts
     * is discarded by {@link #handle(Event)} anyway, so registering for it costs a listener call
     * per event for nothing.
     * </p>
     * <p>
     * Each registration forwards only events of the exact class it was made for. Bukkit keys its
     * handler lists on whichever class declares one, so a registration made for an event class
     * that shares its handler list with a relative also receives that relative's events — without
     * this filter a skill declaring triggers for both would see one event twice.
     * </p>
     */
    public void register() {
        log.debug("Registering skill: " + getName());
        for (Class<? extends Event> trigger : handlers.keySet()) {
            log.debug("Registering trigger " + trigger.getSimpleName());
            final EventExecutor executor = (listener, event) -> {
                if (event.getClass() == trigger) handle(event);
            };
            getPluginManager().registerEvent(
                    trigger, this, EventPriority.MONITOR, executor, simpleSkills
            );
            log.debug("Registered trigger " + trigger.getSimpleName() + " for skill " + getName());
        }
    }

    /**
     * Method to obtain the plugin manager the skill registers its listeners with.
     * <p>
     * {@link Bukkit#getPluginManager()} reads static server state that no unit test has a server
     * to provide, so this indirection exists for tests to substitute a plugin manager.
     * </p>
     *
     * @return the server's {@link PluginManager}.
     */
    PluginManager getPluginManager() {
        return Bukkit.getPluginManager();
    }

    // Id-generation and equality overriding

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AbstractSkill that = (AbstractSkill) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    // Abstract methods

    /**
     * Method to get the chance of a skill incrementing or levelling.
     *
     * @return double chance (1-100).
     * @see #randomExpGainChance()
     */
    public abstract double getChance();

    /**
     * Method to determine if a skill is chance-incremented/levelled.
     *
     * @return {@code true} or {@code false}.
     */
    public abstract boolean randomExpGainChance();

    /**
     * Method to reward the player at their level.
     *
     * @param player    to reward.
     * @param skillData assigned data to the skill reward, 'Block' for 'BlockSkills' etc.
     */
    public abstract void executeReward(@NotNull Player player, Object... skillData);

    /**
     * Method to send the skill information to the command sender.
     *
     * @param commandSender to send the skill info to.
     */
    public void sendInfo(CommandSender commandSender) {
        for (String sinfo : messageService.getlang().getStringList("Skill-Info"))
            commandSender.sendMessage(messageService.convert(sinfo)
                    .replaceAll("%skillname%", getName())
                    .replaceAll("%active%", String.valueOf(isActive()))
                    .replaceAll("%mlevel%", String.valueOf(getMaxLevel()))
                    .replaceAll("%ber%", String.valueOf(getExpRequirement()))
                    .replaceAll("%eif%", String.valueOf(getExpFactor())));
    }

}
