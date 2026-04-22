package org.dreambot.powerslayer;

import org.dreambot.powerslayer.abstracts.State;
import org.dreambot.powerslayer.common.MethodBase;
import org.dreambot.powerslayer.data.SlayerMaster;
import org.dreambot.powerslayer.states.BankingState;
import org.dreambot.powerslayer.states.FighterState;
import org.dreambot.powerslayer.states.GetTaskState;
import org.dreambot.powerslayer.states.GoToBankState;
import org.dreambot.powerslayer.states.GoToMasterState;
import org.dreambot.powerslayer.states.GoToMonsterState;
import org.dreambot.powerslayer.wrappers.Task;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.MouseListener;
import org.dreambot.api.wrappers.widgets.message.Message;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

@ScriptManifest(author = "Powerbot Scripters Team", name = "Power Slayer", version = 1.00, category = Category.SLAYER, description = "The best universal slayer bot!")
public class PowerSlayer extends AbstractScript implements ChatListener, MouseListener {

	public static Task currentTask;
	public SlayerMaster slayerMaster;

	private ArrayList<State> states = new ArrayList<State>();
	public MethodBase methodBase = null;

	private int tab = 1;
	public Paint paint;


	@Override
	public void onStart() {
		//TODO: Decide where a player must start the script
		initalizeMethodBase();
		initStates();
		paint = new Paint();
	}

	@Override
	public int onLoop() {
		// Loop through every state, first one active will be executed.
		for (State state : states) {
			if (state.activeCondition()) {
				return state.loop();
			}
		}
		return -1;
	}

	public void initalizeMethodBase() {
		if (methodBase == null) {
			methodBase = new MethodBase(this);
		}
	}

	public void initStates() {
		states.add(new GetTaskState(methodBase));
		states.add(new GoToMasterState(methodBase));
		states.add(new GoToBankState(methodBase));
		states.add(new GoToMonsterState(methodBase));
		states.add(new BankingState(methodBase));
		states.add(new FighterState(methodBase));
	}

	@SuppressWarnings("unused")
	private int getStateLoop() {
		for (State state : states) {
			if (state.activeCondition()) {
				return state.loop();
			}
		}
		return -1;
	}

	@Override
	public void onGameMessage(Message message) {
		if (message.getMessage().equals("You can't reach that.")) {
			if (methodBase.fighter.loot.itemWasClickedLast && methodBase.fighter.loot.lastClickedItem != null) {
				methodBase.fighter.tiles.addBadTile(methodBase.fighter.loot.lastClickedItem.getTile());
			} else if (methodBase.fighter.npcs.npcWasClickedLast && methodBase.fighter.npcs.lastClickedNPC != null) {
				methodBase.fighter.tiles.addBadTile(methodBase.fighter.npcs.lastClickedNPC.getTile());
			}
		} else if (message.getMessage().equals("You don't have any quick prayers selected.")) {
			methodBase.fighter.pot.setQuickPrayer = false;
			log("You must set your quick prayers to use prayer potions.");
		}
	}

	@Override
	public void onPublicMessage(Message message) {
	}

	@Override
	public void onPrivateMessage(Message message) {
	}

	@Override
	public void onTradeMessage(Message message) {
	}

	//Start Paint

	public class Paint {
		public String Current = "Loading...";
		public Image closed;
		public Image tabOne;
		public Image tabTwo;
		public final Rectangle hideRect = new Rectangle(477, 336, 34, 37);
		public final Rectangle tabOneRect = new Rectangle(177, 335, 147, 37);
		public final Rectangle tabTwoRect = new Rectangle(327, 336, 148, 37);

		public Paint () {
			URL resource = this.getClass().getClassLoader().getResource("/resources/slosedc.png");
			if (resource != null) {
				try {
					closed = ImageIO.read(resource);
					resource = this.getClass().getClassLoader().getResource("/resources/gentab.png");
					tabOne = ImageIO.read(resource);
					resource = this.getClass().getClassLoader().getResource("/resources/exptab.png");
					tabTwo = ImageIO.read(resource);
				} catch (Exception ignored) {}
			} else {
				closed = getImage("https://github.com/Zalgo2462/PowerSlayer/tree/master/resources/closedc.png");
				tabOne = getImage("https://github.com/Zalgo2462/PowerSlayer/tree/master/resources/gentab.png");
				tabTwo = getImage("https://github.com/Zalgo2462/PowerSlayer/tree/master/resources/exptab.png");
			}
		}

		private Image getImage(String url) {
			try {
				return ImageIO.read(new URL(url));
			} catch (IOException e) {
				return null;
			}
		}
	}

	enum PaintSkill {
		ATTACK(Skill.ATTACK, "Attack", 1),
		HITPOINTS(Skill.HITPOINTS, "Hitpoints", 4),
		DEFENCE(Skill.DEFENCE, "Defence", 3),
		MAGIC(Skill.MAGIC, "Magic", 6),
		RANGED(Skill.RANGED, "Ranged", 5),
		SLAYER(Skill.SLAYER, "Slayer", 0),
		STRENGTH(Skill.STRENGTH, "Strength", 2);

		Skill skill;
		String skillName;
		int index;

		private PaintSkill(Skill skill, String skillName, int index) {
			this.skill = skill;
			this.skillName = skillName;
			this.index = index;
		}
	}

	@Override
	public void onPaint(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (tab == 1) {
			g.drawImage(paint.tabOne, -1, 293, null);
		} else if (tab == 2) {
			g.drawImage(paint.tabTwo, -1, 293, null);
			drawSkillBars(g);
		} else {
			g.drawImage(paint.closed, 162, 293, null);
		}
	}

	private void drawSkillBars(Graphics g) {
		for (PaintSkill s : PaintSkill.values()) {
			int x = s.index <= 3 ? 20 : 180;
			int y = s.index <= 3 ? 390 + (s.index * 20)
					: 390 + ((s.index - 3) * 20);
			g.setColor(new Color(153, 153, 153));
			g.drawRect(x, y, 150, 15);
			g.setColor(new Color(0, 0, 0, 80));
			g.fillRect(x, y,
					(int) (Skills.getPercentageToLevel(s.skill) * 1.5), 15);
			g.setColor(new Color(90, 15, 15));
			g.setFont(new Font("Serif", 0, 12));
			g.drawString(
					s.skillName + ": "
					+ Skills.getPercentageToLevel(s.skill)
					+ "% to level "
					+ (Skills.getRealLevel(s.skill) + 1), x + 4,
					y + 12);
			g.setColor(new Color(255, 255, 255, 90));
			g.drawString(
					s.skillName + ": "
					+ Skills.getPercentageToLevel(s.skill)
					+ "% to level "
					+ (Skills.getRealLevel(s.skill) + 1), x + 5,
					y + 13);
		}
	}

	@Override
	public void onClick(MouseEvent e) {
		if (paint.hideRect.contains(e.getPoint())) {
			tab = 3;
		} else if (paint.tabOneRect.contains(e.getPoint())) {
			tab = 1;
		} else if (paint.tabTwoRect.contains(e.getPoint())) {
			tab = 2;
		}
	}

	@Override
	public void onPress(MouseEvent e) {
	}

	@Override
	public void onRelease(MouseEvent e) {
	}

	@Override
	public void onEnter(MouseEvent e) {
	}

	@Override
	public void onExit(MouseEvent e) {
	}

	@Override
	public void onMove(MouseEvent e) {
	}

	@Override
	public void onDrag(MouseEvent e) {
	}
}
