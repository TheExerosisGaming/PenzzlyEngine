package com.penzzly.engine.core.mini.partial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import static org.bukkit.ChatColor.getLastColors;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import static org.bukkit.scoreboard.DisplaySlot.SIDEBAR;

public class MiniScoreboards {
	public interface Scoreboard {
		/**
		 * Sets the line at a given index to the given text.
		 * @param index - The index of the line.
		 * @param text The text. ~32 characters max.
		 * @return - The index of the line.
		 */
		int line(int index, Object text);
		
		/**
		 * Sets the line at the next index to a blank.
		 * @param text The text. ~32 characters max.
		 * @return - The index of the line.
		 */
		int line(Object text);
		
		/**
		 * Sets the line at a given index to a blank.
		 * @param index The index of the line.
		 * @return - The index of the line.
		 */
		default int blank(int index) {
			return line(index, "");
		}
		
		/**
		 * Sets the line at the next index to a blank.
		 * @return - The index of the line.
		 */
		default int blank() {
			return line("");
		}
		
		
		/**
		 * Removes the line at the given index.
		 * @param index The index of the line.
		 * @return - {@code true} if the line was previously set.
		 */
		boolean remove(int index);
		
		/**
		 * Sets the title to the given value.
		 * @param title The title.
		 */
		void title(Object title);
		
		
		/**
		 * Returns the underlying {@link org.bukkit.scoreboard.Scoreboard}.
		 *
		 * @return - The underlying {@link org.bukkit.scoreboard.Scoreboard}.
		 */
		org.bukkit.scoreboard.Scoreboard getScoreboard();
	}
	
	/**
	 * A 32 character no flicker scoreboard implementation, fast and lightweight.
	 */
	public static class TeamsScoreboard implements Scoreboard {
		public static final int MAX_LINES = 15;
		private static final String ERROR = "[Scoreboard] A %d character line was truncated to 32 characters. Line: %d Text: '%s'%n";
		private static final String[] BLANKS = new String[MAX_LINES];
		private final Objective objective;
		private final org.bukkit.scoreboard.Scoreboard board;
		private final Team[] teams = new Team[15];
		private final boolean[] set = new boolean[15];
		
		static {
			ChatColor[] colors = ChatColor.values();
			for (int i = 0; i < BLANKS.length; i++)
				BLANKS[i] = colors[i].toString();
		}
		
		public TeamsScoreboard() {
			
			this.board = Bukkit.getScoreboardManager().getNewScoreboard();
			objective = board.registerNewObjective("test", "dummy");
			objective.setDisplaySlot(SIDEBAR);
		}
		
		@Override
		public int line(int index, Object line) {
			String colored = translateAlternateColorCodes('&', line.toString());
			if (colored.length() > 32) {
				System.err.printf(ERROR, colored.length(), index, colored);
				colored = colored.substring(0, 32);
			}
			
			Team team = teams[index];
			if (team == null) {
				team = teams[index] = board.registerNewTeam(String.valueOf(index));
				team.addEntry(BLANKS[index]);
			}
			
			boolean both = colored.length() > 16;
			String prefix = both ? colored.substring(0, 16) : colored;
			team.setPrefix(prefix);
			if (both) {
				String lastColors = getLastColors(prefix);
				team.setSuffix(lastColors + colored.substring(16, colored.length()));
			}
			
			if (!set[index]) {
				objective.getScore(BLANKS[index]).setScore((MAX_LINES - 1) - index);
				set[index] = true;
			}
			return index;
		}
		
		@Override
		public int line(Object line) {
			for (int i = 0; i < MAX_LINES; i++)
				if (!set[i]) {
					return line(i, line);
				}
			return line(MAX_LINES - 1, line);
		}
		
		@Override
		public boolean remove(int index) {
			if (index < MAX_LINES && set[index]) {
				board.resetScores(BLANKS[index]);
				set[index] = false;
				return true;
			}
			return false;
		}
		
		@Override
		public void title(Object title) {
			objective.setDisplayName(translateAlternateColorCodes('&', title.toString()));
		}
		
		@Override
		public org.bukkit.scoreboard.Scoreboard getScoreboard() {
			return board;
		}
	}
	
	/**
	 * A 32 character no flicker scoreboard implementation, fast and lightweight.
	 *//*
	public static class FullLengthScoreboard implements Scoreboard {
		private static final int MAX_LINES = 15;
		private static final String[][] BLANKS = new String[MAX_LINES][MAX_LINES];
		private static final int[] CODES = new int[67];
		
		static {
			ChatColor[] colors = ChatColor.values();
			for (int i = 0; i < MAX_LINES; i++)
				for (int j = 0; j < MAX_LINES; j++)
					BLANKS[i][j] = colors[i].toString() + colors[j];
			
			for (int i = 0; i < colors.length; i++) {
				ChatColor color = colors[i];
				CODES[color.getChar() - 48] = color.ordinal();
				CODES[Character.toUpperCase(color.getChar()) - 48] = color.ordinal();
			}
		}
		
		private final Objective objective;
		private final org.bukkit.scoreboard.Scoreboard board;
		private final Team[] teams = new Team[15];
		private final byte[] lastColors = new byte[15];
		private final byte[] colorDepths = new byte[15];
		private final String[] entries = new
		private final List<Integer> shown = new ArrayList<>();
		
		public FullLengthScoreboard() {
			fill(lastColors, (byte) -1);
			this.board = Bukkit.getScoreboardManager().getNewScoreboard();
			objective = board.registerNewObjective("test", "dummy");
			objective.setDisplaySlot(SIDEBAR);
		}
		
		private static char[] lastColor(char colorChar, Object line) {
			int lastColor = -1;
			char[] chars = (" " + line).toCharArray();
			for (int i = 1; i < chars.length - 1; i++) {
				char c = chars[i];
				boolean translate = c == colorChar;
				if (translate || c == '§') {
					int next = chars[i + 1] - 48;
					if (next < 67 && next >= 0) {
						if (translate) {
							chars[i] = '§';
						}
						if (next < 59) {
							lastColor = CODES[next - 48];
						}
					}
				}
			}
			chars[0] = (char) lastColor;
			return chars;
		}
		
		
		@Override
		public int line(int index, Object line) {
			char[] chars = lastColor('&', line);
			String colored = new String(chars, 1, chars.length);
			
			int color = chars[0];
			byte lastColor = lastColors[index];
			if (lastColor != color) {
				colorDepths[lastColor]--;
				String entryName = BLANKS[color][colorDepths[color]++];
				
			}
			
			Team team = teams[index];
			if (getLastColors(l)) {
				if (team == null) {
					team = board.registerNewTeam(String.valueOf(index));
					String entry = getNextEntry();
					team.addEntry(entry);
					colorDepths.put(index, entry);
				}
			}
			Team team = teams[index];
			if (team == null) {
				team = teams[index] = board.registerNewTeam(String.valueOf(index));
				team.addEntry(BLANKS[index]);
			}
			
			boolean both = colored.length() > 16;
			String prefix = both ? colored.substring(0, 16) : colored;
			team.setPrefix(prefix);
			if (both) {
				String lastColors = getLastColors(prefix);
				team.setSuffix(lastColors + colored.substring(16, colored.length()));
			}
			
			if (!set[index]) {
				objective.getScore(BLANKS[index]).setScore((MAX_LINES - 1) - index);
				set[index] = true;
			}
			return index;
		}
		
		@Override
		public int add(Object line) {
			return 0;
		}
		
		@Override
		public void remove(int score) {
			if (colorDepths.containsKey(score)) {
				board.resetScores(colorDepths.get(score));
			}
			if (shown.contains(score)) {
				shown.remove(Integer.valueOf(score));
			}
		}
		
		@Override
		public void setTitle(Object title) {
			objective.setDisplayName(translateAlternateColorCodes('&', title.toString()));
		}
		
		@Override
		public org.bukkit.scoreboard.Scoreboard getScoreboard() {
			return board;
		}
	}*/
}