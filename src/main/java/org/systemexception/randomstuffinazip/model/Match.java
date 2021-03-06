package org.systemexception.randomstuffinazip.model;

import org.apache.commons.io.FileUtils;
import org.systemexception.logger.api.Logger;
import org.systemexception.logger.impl.LoggerImpl;
import org.systemexception.randomstuffinazip.pojo.XmlValidator;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author leo
 * @date 24/07/15 22:18
 */
public class Match {

	private final static Logger logger = LoggerImpl.getFor(Match.class);
	private final String xmlHeader = "<match><match_id>$MATCH_ID</match_id>", xmlFooter = "</match>";
	private final String player = "<playername>$PLAYER</playername>";
	private final String points = "<playerpoints>$POINTS</playerpoints>";
	private final String xsdFilePath = "xsd" + File.separator + "MatchPoints.xsd";
	private final ArrayList<ArrayList<String>> playerPoints = new ArrayList<>();
	private final int matchId;

	public Match() {
		this.matchId = getRandomMatchId();
		logger.info("Generated match:" + matchId);
	}

	/**
	 * Adds a player to the match
	 *
	 * @param player is the player to be added to the match
	 */
	public void addPlayer(final Player player) {
		ArrayList<String> playerScore = new ArrayList<>();
		playerScore.add(player.getName());
		playerScore.add(String.valueOf(player.getPoints()));
		playerPoints.add(playerScore);
		logger.info("Added player " + player.getName() + " to match " + matchId);
	}

	public void saveMatchToFile(final String outputPath) throws IOException {
		FileUtils.writeStringToFile(new File(outputPath + File.separator + matchId + ".xml"), matchToXml());
	}

	/**
	 * Generates the match xml and validates against an xsd
	 *
	 * @return the xml string
	 */
	public String matchToXml() {
		String xml = xmlHeader.replace("$MATCH_ID", String.valueOf(getRandomMatchId()));
		for (ArrayList<String> playerScore : playerPoints) {
			xml = xml.concat("<playerscore>");
			xml = xml.concat(player.replace("$PLAYER", playerScore.get(0))).concat(points.replace("$POINTS", String
					.valueOf(playerScore.get(1))));
			xml = xml.concat("</playerscore>");
		}
		xml = xml.concat(xmlFooter);
		XmlValidator xmlValidator = new XmlValidator(xml, xsdFilePath);
		try {
			xmlValidator.validateXml();
		} catch (SAXException | IOException e) {
			logger.error("Error in XML generation", e);
		}
		return xml;
	}

	private int getRandomMatchId() {
		Random rnd = new Random();
		return rnd.nextInt(Integer.MAX_VALUE) + 1;
	}

	public int getMatchId() {
		return matchId;
	}
}
