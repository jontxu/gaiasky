package gaia.cu9.ari.gaiaorbit.interfce;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.BufferUtils;
import gaia.cu9.ari.gaiaorbit.event.EventManager;
import gaia.cu9.ari.gaiaorbit.event.Events;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.GlobalResources;
import gaia.cu9.ari.gaiaorbit.util.scene2d.*;
import gaia.cu9.ari.gaiaorbit.util.update.VersionCheckEvent;
import gaia.cu9.ari.gaiaorbit.util.update.VersionChecker;

import java.nio.IntBuffer;
import java.time.Instant;
import java.util.Date;

/**
 * The help window with About, Help and System sections.
 *
 * @author tsagrista
 */
public class AboutWindow extends GenericDialog {

    private LabelStyle linkStyle;
    private Table checkTable;
    private OwnLabel checkLabel;

    public AboutWindow(Stage stage, Skin skin) {
        super(txt("gui.help.help") + " - " + GlobalConf.version.version + " - " + txt("gui.build", GlobalConf.version.build), skin, stage);
        this.linkStyle = skin.get("link", LabelStyle.class);

        setCancelText(txt("gui.close"));

        // Build
        buildSuper();

    }

    @Override
    protected void build() {
        float tawidth = 440 * GlobalConf.SCALE_FACTOR;
        float tawidth2 = 800 * GlobalConf.SCALE_FACTOR;
        float taheight = 250 * GlobalConf.SCALE_FACTOR;
        float taheight_s = 60 * GlobalConf.SCALE_FACTOR;
        float tabwidth = 110 * GlobalConf.SCALE_FACTOR;

        // Create the tab buttons
        HorizontalGroup group = new HorizontalGroup();
        group.align(Align.left);

        final Button tabHelp = new OwnTextButton(txt("gui.help.help"), skin, "toggle-big");
        tabHelp.pad(pad5);
        tabHelp.setWidth(tabwidth);
        final Button tabAbout = new OwnTextButton(txt("gui.help.about"), skin, "toggle-big");
        tabAbout.pad(pad5);
        tabAbout.setWidth(tabwidth);
        final Button tabSystem = new OwnTextButton(txt("gui.help.system"), skin, "toggle-big");
        tabSystem.pad(pad5);
        tabSystem.setWidth(tabwidth);
        final Button tabUpdates = new OwnTextButton(txt("gui.newversion"), skin, "toggle-big");
        tabUpdates.pad(pad5);
        tabUpdates.setWidth(tabwidth);

        group.addActor(tabHelp);
        group.addActor(tabAbout);
        group.addActor(tabSystem);
        group.addActor(tabUpdates);
        content.add(group).align(Align.left).padLeft(pad5);
        content.row();
        content.pad(pad);

        // Create the tab content. Just using images here for simplicity.
        Stack tabContent = new Stack();

        /** CONTENT 1 - HELP **/
        final Table contentHelp = new Table(skin);
        contentHelp.align(Align.top | Align.left);

        FileHandle gslogo = Gdx.files.internal(GlobalConf.SCALE_FACTOR > 1.5f ? "img/gaiasky-logo.png" : "img/gaiasky-logo-s.png");
        Texture logotex = new Texture(gslogo);
        logotex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        Image gaiasky = new Image(logotex);
        gaiasky.setOrigin(Align.center);

        // User manual
        Label usermantitle = new OwnLabel(txt("gui.help.usermanual"), skin);
        Label usermantxt = new OwnLabel(txt("gui.help.help1"), skin);
        Link usermanlink = new Link(GlobalConf.WEBPAGE, linkStyle, GlobalConf.WEBPAGE);

        // Wiki
        Label wikititle = new OwnLabel("Docs", skin);
        Label wikitxt = new OwnLabel(txt("gui.help.help2"), skin);
        Link wikilink = new Link(GlobalConf.DOCUMENTATION, linkStyle, GlobalConf.DOCUMENTATION);

        // Readme
        Label readmetitle = new OwnLabel(txt("gui.help.readme"), skin);
        FileHandle readmefile = Gdx.files.internal("README.md");
        if (!readmefile.exists()) {
            readmefile = Gdx.files.internal("../README.md");
        }
        String readmestr = readmefile.readString();
        int lines = GlobalResources.countOccurrences(readmestr, '\n');
        OwnTextArea readme = new OwnTextArea(readmestr, skin, "no-disabled");
        readme.setDisabled(true);
        readme.setPrefRows(lines);
        readme.clearListeners();
        if (GlobalConf.SCALE_FACTOR < 1.5f)
            readme.setWidth(tawidth * 1.3f);

        OwnScrollPane readmescroll = new OwnScrollPane(readme, skin, "minimalist-nobg");
        readmescroll.setWidth(tawidth);
        readmescroll.setHeight(taheight);
        readmescroll.setForceScroll(false, true);
        readmescroll.setSmoothScrolling(true);
        readmescroll.setFadeScrollBars(false);
        if (GlobalConf.SCALE_FACTOR < 1.5f)
            readmescroll.setWidth(tawidth * 1.35f);

        scrolls.add(readmescroll);

        // Add all to content
        contentHelp.add(gaiasky).pad(pad * 2).colspan(2);
        contentHelp.row();
        contentHelp.add(usermantitle).align(Align.left).padRight(pad);
        contentHelp.add(usermantxt).align(Align.left);
        contentHelp.row();
        contentHelp.add(new OwnLabel("", skin));
        contentHelp.add(usermanlink).align(Align.left);
        contentHelp.row();
        contentHelp.add(wikititle).align(Align.left).padRight(pad);
        contentHelp.add(wikitxt).align(Align.left);
        contentHelp.row();
        contentHelp.add(new OwnLabel("", skin));
        contentHelp.add(wikilink).align(Align.left);
        contentHelp.row();
        contentHelp.add(readmetitle).colspan(2).align(Align.left);
        contentHelp.row();
        contentHelp.add(readmescroll).colspan(2).expand().pad(pad, 0, pad, 0).align(Align.center);

        /** CONTENT 2 - ABOUT **/
        final Table contentAbout = new Table(skin);
        contentAbout.align(Align.top);

        // Intro
        TextArea intro = new OwnTextArea(txt("gui.help.gscredits", GlobalConf.version.version), skin.get("regular", TextFieldStyle.class));
        intro.setDisabled(true);
        intro.setPrefRows(3);
        intro.setWidth(tawidth);

        // Home page
        Label homepagetitle = new OwnLabel(txt("gui.help.homepage"), skin);
        Link homepage = new Link(GlobalConf.WEBPAGE, linkStyle, GlobalConf.WEBPAGE);

        // Author
        Label authortitle = new OwnLabel(txt("gui.help.author"), skin);

        VerticalGroup author = new VerticalGroup();
        author.align(Align.left);
        Label authorname = new OwnLabel(GlobalConf.AUTHOR_NAME, skin);
        Link authormail = new Link(GlobalConf.AUTHOR_EMAIL, linkStyle, "mailto:" + GlobalConf.AUTHOR_EMAIL);
        Link authorpage = new Link("www.tonisagrista.com", linkStyle, "https://tonisagrista.com");
        author.addActor(authorname);
        author.addActor(authormail);
        author.addActor(authorpage);

        // Contributor
        Label contribtitle = new OwnLabel(txt("gui.help.contributors"), skin);

        VerticalGroup contrib = new VerticalGroup();
        contrib.align(Align.left);
        Label contribname = new OwnLabel("Apl. Prof. Dr. Stefan Jordan", skin);
        Link contribmail = new Link("jordan@ari.uni-heidelberg.de", linkStyle, "mailto:jordan@ari.uni-heidelberg.de");
        contrib.addActor(contribname);
        contrib.addActor(contribmail);

        // License
        HorizontalGroup licenseh = new HorizontalGroup();
        licenseh.space(pad * 2);

        VerticalGroup licensev = new VerticalGroup();
        TextArea licensetext = new OwnTextArea(txt("gui.help.license"), skin.get("regular", TextFieldStyle.class));
        licensetext.setDisabled(true);
        licensetext.setPrefRows(3);
        licensetext.setWidth(tawidth2 / 2f);
        Link licenselink = new Link("https://opensource.org/licenses/MPL-2.0", linkStyle, "https://opensource.org/licenses/MPL-2.0");

        licensev.addActor(licensetext);
        licensev.addActor(licenselink);

        licenseh.addActor(licensev);

        // Thanks

        HorizontalGroup thanks = new HorizontalGroup();
        thanks.space(pad * 2);
        Container<Actor> thanksc = new Container<Actor>(thanks);
        thanksc.setBackground(skin.getDrawable("bg-clear"));

        Image zah = new Image(getSpriteDrawable(Gdx.files.internal("img/zah.png")));
        Image dlr = new Image(getSpriteDrawable(Gdx.files.internal("img/dlr.png")));
        Image bwt = new Image(getSpriteDrawable(Gdx.files.internal("img/bwt.png")));
        Image dpac = new Image(getSpriteDrawable(Gdx.files.internal("img/dpac.png")));

        thanks.addActor(zah);
        thanks.addActor(dlr);
        thanks.addActor(bwt);
        thanks.addActor(dpac);

        contentAbout.add(intro).colspan(2).align(Align.left).padTop(pad);
        contentAbout.row();
        contentAbout.add(homepagetitle).align(Align.topLeft).padRight(pad);
        contentAbout.add(homepage).align(Align.left);
        contentAbout.row();
        contentAbout.add(authortitle).align(Align.topLeft).padRight(pad).padTop(pad5);
        contentAbout.add(author).align(Align.left).padTop(pad5);
        contentAbout.row();
        contentAbout.add(contribtitle).align(Align.topLeft).padRight(pad).padTop(pad5);
        contentAbout.add(contrib).align(Align.left).padTop(pad5);
        contentAbout.row();
        contentAbout.add(licenseh).colspan(2).align(Align.center).padTop(pad * 2);
        contentAbout.row();
        contentAbout.add(thanksc).colspan(2).align(Align.center).padTop(pad * 4);

        /** CONTENT 3 - SYSTEM **/
        final Table contentSystem = new Table(skin);
        contentSystem.align(Align.top | Align.left);

        // Build info
        Label buildinfo = new OwnLabel(txt("gui.help.buildinfo"), skin, "help-title");

        Label versiontitle = new OwnLabel(txt("gui.help.version", GlobalConf.APPLICATION_NAME), skin);
        Label version = new OwnLabel(GlobalConf.version.version, skin);

        Label revisiontitle = new OwnLabel(txt("gui.help.buildnumber"), skin);
        Label revision = new OwnLabel(GlobalConf.version.build, skin);

        Label timetitle = new OwnLabel(txt("gui.help.buildtime"), skin);
        Label time = new OwnLabel(GlobalConf.version.buildtime.toString(), skin);

        Label systemtitle = new OwnLabel(txt("gui.help.buildsys"), skin);
        TextArea system = new OwnTextArea(GlobalConf.version.system, skin.get("regular", TextFieldStyle.class));
        system.setDisabled(true);
        system.setPrefRows(3);
        system.setWidth(tawidth * 2f / 3f);

        Label buildertitle = new OwnLabel(txt("gui.help.builder"), skin);
        Label builder = new OwnLabel(GlobalConf.version.builder, skin);

        // Java info
        Label javainfo = new OwnLabel(txt("gui.help.javainfo"), skin, "help-title");

        Label javaversiontitle = new OwnLabel(txt("gui.help.javaversion"), skin);
        Label javaversion = new OwnLabel(System.getProperty("java.version"), skin);

        Label javaruntimetitle = new OwnLabel(txt("gui.help.javaname"), skin);
        Label javaruntime = new OwnLabel(System.getProperty("java.runtime.name"), skin);

        Label javavmnametitle = new OwnLabel(txt("gui.help.javavmname"), skin);
        Label javavmname = new OwnLabel(System.getProperty("java.vm.name"), skin);

        Label javavmversiontitle = new OwnLabel(txt("gui.help.javavmversion"), skin);
        Label javavmversion = new OwnLabel(System.getProperty("java.vm.version"), skin);

        Label javavmvendortitle = new OwnLabel(txt("gui.help.javavmvendor"), skin);
        Label javavmvendor = new OwnLabel(System.getProperty("java.vm.vendor"), skin);

        TextButton memoryinfobutton = new OwnTextButton(txt("gui.help.meminfo"), skin, "default");
        memoryinfobutton.setName("memoryinfo");
        memoryinfobutton.setSize(150 * GlobalConf.SCALE_FACTOR, 20 * GlobalConf.SCALE_FACTOR);
        memoryinfobutton.addListener(event -> {
            if (event instanceof ChangeEvent) {
                EventManager.instance.post(Events.DISPLAY_MEM_INFO_WINDOW);
                return true;
            }
            return false;
        });

        // System info
        Label sysinfo = new OwnLabel(txt("gui.help.sysinfo"), skin, "help-title");

        Label sysostitle = new OwnLabel(txt("gui.help.os"), skin);
        Label sysos = new OwnLabel(System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"), skin);

        Label glrenderertitle = new OwnLabel(txt("gui.help.graphicsdevice"), skin);
        Label glrenderer = new OwnLabel(Gdx.gl.glGetString(GL20.GL_RENDERER), skin);

        // OpenGL info
        Label glinfo = new OwnLabel(txt("gui.help.openglinfo"), skin, "help-title");

        Label glvendortitle = new OwnLabel(txt("gui.help.glvendor"), skin);
        Label glvendor = new OwnLabel(Gdx.gl.glGetString(GL20.GL_VENDOR), skin);

        Label glversiontitle = new OwnLabel(txt("gui.help.openglversion"), skin);
        Label glversion = new OwnLabel(Gdx.gl.glGetString(GL20.GL_VERSION), skin);

        Label glslversiontitle = new OwnLabel(txt("gui.help.glslversion"), skin);
        Label glslversion = new OwnLabel(Gdx.gl.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION), skin);

        Label glextensionstitle = new OwnLabel(txt("gui.help.glextensions"), skin);
        String extensions = Gdx.gl.glGetString(GL20.GL_EXTENSIONS);
        IntBuffer buf = BufferUtils.newIntBuffer(16);
        if (extensions.isEmpty() || extensions == null) {
            Gdx.gl.glGetIntegerv(GL30.GL_NUM_EXTENSIONS, buf);
            int next = buf.get(0);
            String[] extensionsstr = new String[next];
            for (int i = 0; i < next; i++) {
                extensionsstr[i] = Gdx.gl30.glGetStringi(GL30.GL_EXTENSIONS, i);
            }
            extensions = arrayToStr(extensionsstr);
        } else {
            extensions = extensions.replaceAll(" ", "\n");
        }
        Gdx.gl.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, buf);
        int maxSize = buf.get(0);
        lines = GlobalResources.countOccurrences(extensions, '\n');
        OwnTextArea glextensions = new OwnTextArea("Max texture size: " + maxSize + '\n' + extensions, skin, "no-disabled");
        glextensions.setDisabled(true);
        glextensions.setPrefRows(lines);
        glextensions.clearListeners();

        OwnScrollPane glextensionsscroll = new OwnScrollPane(glextensions, skin, "minimalist-nobg");
        glextensionsscroll.setWidth(tawidth / 1.7f);
        glextensionsscroll.setHeight(taheight_s);
        glextensionsscroll.setForceScroll(false, true);
        glextensionsscroll.setSmoothScrolling(true);
        glextensionsscroll.setFadeScrollBars(false);
        scrolls.add(glextensionsscroll);

        contentSystem.add(buildinfo).colspan(2).align(Align.left).padTop(pad * 1.5f).padBottom(pad);
        contentSystem.row();
        contentSystem.add(versiontitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(version).align(Align.left);
        contentSystem.row();
        contentSystem.add(revisiontitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(revision).align(Align.left);
        contentSystem.row();
        contentSystem.add(timetitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(time).align(Align.left);
        contentSystem.row();
        contentSystem.add(buildertitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(builder).align(Align.left).padBottom(pad * 1.5f);
        contentSystem.row();
        contentSystem.add(systemtitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(system).align(Align.left);
        contentSystem.row();

        contentSystem.add(javainfo).colspan(2).align(Align.left).padTop(pad).padBottom(pad);
        contentSystem.row();
        contentSystem.add(javaversiontitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(javaversion).align(Align.left);
        contentSystem.row();
        contentSystem.add(javaruntimetitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(javaruntime).align(Align.left);
        contentSystem.row();
        contentSystem.add(javavmnametitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(javavmname).align(Align.left);
        contentSystem.row();
        contentSystem.add(javavmversiontitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(javavmversion).align(Align.left);
        contentSystem.row();
        contentSystem.add(javavmvendortitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(javavmvendor).align(Align.left).padBottom(pad);
        contentSystem.row();
        contentSystem.add(memoryinfobutton).colspan(2).align(Align.left);
        contentSystem.row();

        contentSystem.add(sysinfo).colspan(2).align(Align.left).padTop(pad).padBottom(pad);
        contentSystem.row();
        contentSystem.add(sysostitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(sysos).align(Align.left);
        contentSystem.row();
        contentSystem.add(glrenderertitle).align(Align.topLeft).padRight(pad);
        contentSystem.add(glrenderer).align(Align.left);
        contentSystem.row();

        contentSystem.add(glinfo).colspan(2).align(Align.left).padTop(pad).padBottom(pad);
        contentSystem.row();
        contentSystem.add(glversiontitle).align(Align.topLeft).padRight(pad );
        contentSystem.add(glversion).align(Align.left);
        contentSystem.row();
        contentSystem.add(glvendortitle).align(Align.topLeft).padRight(pad );
        contentSystem.add(glvendor).align(Align.left);
        contentSystem.row();
        contentSystem.add(glslversiontitle).align(Align.topLeft).padRight(pad );
        contentSystem.add(glslversion).align(Align.left);
        contentSystem.row();
        contentSystem.add(glextensionstitle).align(Align.topLeft).padRight(pad );
        contentSystem.add(glextensionsscroll).align(Align.left);

        /** CONTENT 4 - UPDATES **/
        final Table contentUpdates = new Table(skin);
        contentUpdates.align(Align.top);

        // This is the table that displays it all
        checkTable = new Table(skin);
        checkLabel = new OwnLabel("", skin);

        checkTable.add(checkLabel).top().left().padBottom(pad5).row();
        if (GlobalConf.program.VERSION_LAST_TIME == null || new Date().getTime() - GlobalConf.program.VERSION_LAST_TIME.toEpochMilli() > GlobalConf.ProgramConf.VERSION_CHECK_INTERVAL_MS) {
            // Check!
            checkLabel.setText(txt("gui.newversion.checking"));
            getCheckVersionThread().start();
        } else {
            // Inform latest
            newVersionCheck(GlobalConf.version.version, GlobalConf.version.buildtime);

        }

        contentUpdates.add(checkTable).left().top().padTop(pad * 1.5f);

        /** ADD ALL CONTENT **/
        tabContent.addActor(contentHelp);
        tabContent.addActor(contentAbout);
        tabContent.addActor(contentSystem);
        tabContent.addActor(contentUpdates);

        content.add(tabContent).expand().fill();

        // Listen to changes in the tab button checked states
        // Set visibility of the tab content to match the checked state
        ChangeListener tabListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                contentHelp.setVisible(tabHelp.isChecked());
                contentAbout.setVisible(tabAbout.isChecked());
                contentSystem.setVisible(tabSystem.isChecked());
                contentUpdates.setVisible(tabUpdates.isChecked());
            }
        };
        tabHelp.addListener(tabListener);
        tabAbout.addListener(tabListener);
        tabSystem.addListener(tabListener);
        tabUpdates.addListener(tabListener);

        // Let only one tab button be checked at a time
        ButtonGroup<Button> tabs = new ButtonGroup<Button>();
        tabs.setMinCheckCount(1);
        tabs.setMaxCheckCount(1);
        tabs.add(tabHelp);
        tabs.add(tabAbout);
        tabs.add(tabSystem);
        tabs.add(tabUpdates);

    }

    private String arrayToStr(String[] arr) {
        String buff = new String();
        for (int i = 0; i < arr.length; i++) {
            buff += arr[i] + '\n';
        }
        return buff;
    }

    @Override
    protected void accept() {
    }

    @Override
    protected void cancel() {
    }

    private SpriteDrawable getSpriteDrawable(FileHandle fh) {
        Texture tex = new Texture(fh);
        return new SpriteDrawable(new Sprite(tex));
    }

    /**
     * Checks the given tag time against the current version time and:
     * <ul>
     * <li>Displays a "new version available" message if the given version is
     * newer than the current.</li>
     * <li>Display a "you have the latest version" message and a "check now"
     * button if the given version is older.</li>
     * </ul>
     *
     * @param tagVersion The version to check.
     * @param tagDate    The date
     */
    private void newVersionCheck(String tagVersion, Instant tagDate) {
        GlobalConf.program.VERSION_LAST_TIME = Instant.now();
        if (tagDate.isAfter(GlobalConf.version.buildtime)) {
            // There's a new version!
            checkLabel.setText(txt("gui.newversion.available", GlobalConf.version, tagVersion));
            final String uri = GlobalConf.WEBPAGE_DOWNLOADS;

            OwnTextButton button = new OwnTextButton(txt("gui.newversion.getit"), skin);
            button.pad(pad);
            button.addListener(event -> {
                if (event instanceof ChangeEvent) {
                    Gdx.net.openURI(GlobalConf.WEBPAGE_DOWNLOADS);
                    return true;
                }
                return false;
            });
            checkTable.add(button).center().padBottom(pad5).row();

            Link link = new Link(uri, linkStyle, uri);
            checkTable.add(link).center();

        } else {
            checkLabel.setText(txt("gui.newversion.nonew", GlobalConf.program.getLastCheckedString()));
            // Add check now button
            OwnTextButton button = new OwnTextButton(txt("gui.newversion.checknow"), skin);
            button.addListener(event -> {
                if (event instanceof ChangeEvent) {
                    getCheckVersionThread().start();
                    return true;
                }
                return false;
            });
            checkTable.add(button).center();
        }
    }

    private Thread getCheckVersionThread() {
        // Start version check
        VersionChecker vc = new VersionChecker(GlobalConf.program.VERSION_CHECK_URL);
        vc.setListener(event -> {
            if (event instanceof VersionCheckEvent) {
                VersionCheckEvent vce = (VersionCheckEvent) event;
                if (!vce.isFailed()) {
                    checkTable.clear();
                    checkTable.add(checkLabel).top().left().padBottom(pad5).row();
                    // All is fine
                    newVersionCheck(vce.getTag(), vce.getTagTime());

                } else {
                    // Handle failed case
                    checkLabel.setText(txt("notif.error", "Could not get last version"));
                    checkLabel.setColor(Color.RED);
                }
            }
            return false;
        });
        return new Thread(vc);
    }

}
