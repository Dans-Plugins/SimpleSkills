package dansplugins.simpleskills.skill.abs;

import dansplugins.simpleskills.SimpleSkills;
import dansplugins.simpleskills.playerrecord.PlayerRecordRepository;
import dansplugins.simpleskills.playerrecord.PlayerRecord;
import dansplugins.simpleskills.enums.Triggers;
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
    private final HashMap<Class<? extends Event>, List<Method>> handlers = new HashMap<>();
    private final HashSet<Event> calledEvents = new HashSet<>();
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
        this.active = true;
        this.expReq = this.configService.getConfig().getInt("defaultBaseExperienceRequirement", 10);
        this.expFactor = this.configService.getConfig().getDouble("defaultDefaultExperienceIncreaseFactor", 1.2);
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
     * is compatible with the specific trigger.
     * </p>
     *
     * @param event to handle.
     */
    public void handle(Event event) {
        if (calledEvents.contains(event)) return;
        calledEvents.add(event);
        if (event instanceof Cancellable) if (((Cancellable) event).isCancelled()) return;
        final List<Method> methods = handlers.getOrDefault(event.getClass(), new ArrayList<>());
        for (Method method : methods) {
            try {
                method.invoke(this, event);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
                throw new IllegalStateException("Failed to trigger '" + name + "' with event '" +
                        event.getEventName() + "'!");
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

    // Methods

    public void incrementExperience(@NotNull Player player) {
        if (randomExpGainChance()) {
            final double randomChance = obtainRandomChance();
            if (!(randomChance <= getChance())) return;
        }
        final PlayerRecord playerRecord = getRecord(player);
        if (playerRecord == null) {
            log.info("A player record wasn't found for " + player.getName() + ".");
            return;
        }
        final int skillId = getId();
        playerRecord.incrementExperience(skillId);
    }

    @Nullable
    public PlayerRecord getRecord(@NotNull Player player) {
        return playerRecordRepository.getPlayerRecord(player.getUniqueId());
    }

    public double obtainRandomChance() {
        return Math.random() * 100;
    }

    /**
     * Method to dynamically register every listener defined in {@link Triggers}.
     * <p>
     * This enables the "Skill" class to listen to multiple events for the
     * listener-trigger-hook system.
     * </p>
     */
    public void register() {
        log.info("Registering skill: " + getName());
        final EventExecutor executor = (listener, event) -> handle(event);
        for (Triggers value : Triggers.values()) {
            log.info("Registering trigger " + value.name());
            Bukkit.getPluginManager().registerEvent(
                    value.getTriggerClass(), this, EventPriority.MONITOR, executor, simpleSkills
            );
            log.info("Registered trigger " + value.name() + " for skill " + getName());
        }
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
                    .replaceAll("%mlevel%", String.valueOf(100))
                    .replaceAll("%ber%", String.valueOf(getExpRequirement()))
                    .replaceAll("%eif%", String.valueOf(getExpFactor())));
    }

}
