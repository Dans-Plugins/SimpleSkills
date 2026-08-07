package dansplugins.simpleskills.message;

import dansplugins.simpleskills.SimpleSkills;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Characterizes {@link MessageService}'s handling of a message.yml written to disk by an
 * older version of the plugin: keys added afterwards are absent from it, and skills look
 * them up through {@code Objects.requireNonNull}, so a null lookup previously failed the
 * whole event handler that reached it.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class MessageServiceTest {

    /**
     * The message file bundled in the plugin jar, holding a key the on-disk file predates.
     */
    private static final String BUNDLED_LANG = String.join("\n",
            "message-version: 0.2",
            "Help-Command:",
            "  - \"&9/ss help - bundled\"",
            "Skills:",
            "  Quarrying:",
            "    DoubleDrop: \"&bbundled double drop\"",
            "    Exp: \"&bYou found &a%exp%&b experience while quarrying!\"");

    /**
     * The message file an older version of the plugin left on disk: no Quarrying Exp key, and
     * a customised double drop message.
     */
    private static final String OUTDATED_ON_DISK_LANG = String.join("\n",
            "message-version: 0.1",
            "Skills:",
            "  Quarrying:",
            "    DoubleDrop: \"&bcustomised double drop\"");

    @Rule
    public TemporaryFolder dataFolder = new TemporaryFolder();

    @Mock
    private SimpleSkills simpleSkills;

    private TestMessageService messageService;

    @Before
    public void setUp() throws IOException {
        writeOnDiskLang(OUTDATED_ON_DISK_LANG);
        messageService = new TestMessageService(simpleSkills, onDiskLang());
    }

    @Test
    public void createlang_fallsBackToBundledValue_whenOnDiskFileIsMissingTheKey() {
        messageService.createlang();

        assertEquals("&bYou found &a%exp%&b experience while quarrying!",
                messageService.getlang().getString("Skills.Quarrying.Exp"));
    }

    @Test
    public void createlang_fallsBackToBundledList_whenOnDiskFileIsMissingTheKey() {
        messageService.createlang();

        assertEquals(Collections.singletonList("&9/ss help - bundled"),
                messageService.getlang().getStringList("Help-Command"));
    }

    @Test
    public void createlang_keepsOnDiskValue_whenTheKeyIsPresent() {
        messageService.createlang();

        assertEquals("&bcustomised double drop",
                messageService.getlang().getString("Skills.Quarrying.DoubleDrop"));
    }

    @Test
    public void createlang_leavesTheKeyMissing_whenTheJarHasNoBundledMessageFile() {
        messageService.bundledLang = null;

        messageService.createlang();

        assertNull(messageService.getlang().getString("Skills.Quarrying.Exp"));
    }

    @Test
    public void reloadlang_fallsBackToBundledValue_whenOnDiskFileIsMissingTheKey() {
        messageService.createlang();

        messageService.reloadlang();

        assertEquals("&bYou found &a%exp%&b experience while quarrying!",
                messageService.getlang().getString("Skills.Quarrying.Exp"));
    }

    @Test
    public void savelang_doesNotWriteBundledDefaultsToDisk() throws IOException {
        messageService.createlang();

        messageService.savelang();

        assertNull(YamlConfiguration.loadConfiguration(onDiskLang()).getString("Skills.Quarrying.Exp"));
        assertFalse(readOnDiskLang().contains("experience while quarrying"));
    }

    private void writeOnDiskLang(String contents) throws IOException {
        Files.write(onDiskLang().toPath(), contents.getBytes(StandardCharsets.UTF_8));
    }

    private String readOnDiskLang() throws IOException {
        return new String(Files.readAllBytes(onDiskLang().toPath()), StandardCharsets.UTF_8);
    }

    private File onDiskLang() {
        return new File(dataFolder.getRoot(), "message.yml");
    }

    /**
     * {@link MessageService} reading a temporary message file and a fixed bundled message file,
     * since {@link org.bukkit.plugin.java.JavaPlugin}'s data folder and resource lookups cannot
     * be mocked.
     */
    private static class TestMessageService extends MessageService {
        private final File langFile;
        private String bundledLang = BUNDLED_LANG;

        TestMessageService(SimpleSkills simpleSkills, File langFile) {
            super(simpleSkills);
            this.langFile = langFile;
        }

        @Override
        File resolveLangFile() {
            return langFile;
        }

        @Override
        InputStream openBundledLang() {
            if (bundledLang == null) return null;
            return new ByteArrayInputStream(bundledLang.getBytes(StandardCharsets.UTF_8));
        }
    }
}
