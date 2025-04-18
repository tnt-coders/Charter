package log.charter.gui.panes.songSettings;

import static java.lang.Math.max;
import static log.charter.data.ChordTemplateFingerSetter.setSuggestedFingers;
import static log.charter.data.song.configs.Tuning.getStringDistanceFromC0;
import static log.charter.gui.components.utils.TextInputSelectAllOnFocus.addSelectTextOnFocus;
import static log.charter.util.SoundUtils.soundToFullName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import log.charter.data.ChartData;
import log.charter.data.config.Localization.Label;
import log.charter.data.config.values.InstrumentConfig;
import log.charter.data.song.Arrangement;
import log.charter.data.song.Arrangement.ArrangementSubtype;
import log.charter.data.song.ChordTemplate;
import log.charter.data.song.configs.Tuning;
import log.charter.data.song.configs.Tuning.TuningType;
import log.charter.gui.CharterFrame;
import log.charter.gui.components.containers.ParamsPane;
import log.charter.gui.components.simple.CharterSelect;
import log.charter.gui.components.simple.FieldWithLabel;
import log.charter.gui.components.simple.FieldWithLabel.LabelPosition;
import log.charter.gui.components.simple.TextInputWithValidation;
import log.charter.gui.components.utils.validators.IntegerValueValidator;
import log.charter.gui.menuHandlers.CharterMenuBar;
import log.charter.io.rs.xml.song.ArrangementType;
import log.charter.services.data.selection.SelectionManager;
import log.charter.sound.data.AudioUtils;

public class ArrangementSettingsPane extends ParamsPane {
	private static final long serialVersionUID = -3193534671039163160L;

	private final CharterMenuBar charterMenuBar;
	private final ChartData data;
	private final CharterFrame frame;
	private final SelectionManager selectionManager;

	private final JLabel centOffsetLabel;
	private CharterSelect<TuningType> tuningSelect;
	private TextInputWithValidation stringsInput;
	private FieldWithLabel<JCheckBox> pickedBassField;
	private final int tuningInputsRow;
	private final List<TextInputWithValidation> tuningInputs = new ArrayList<>();
	private final List<JLabel> tuningLabels = new ArrayList<>();
	private FieldWithLabel<JCheckBox> moveFrets;

	private ArrangementType arrangementType;
	private ArrangementSubtype arrangementSubtype;
	private String startingTone;
	private BigDecimal centOffset;
	private Tuning tuning;
	private int capo;
	private boolean pickedBass;

	boolean ignoreEvents = false;

	public ArrangementSettingsPane(final CharterMenuBar charterMenuBar, final ChartData data, final CharterFrame frame,
			final SelectionManager selectionManager, final Runnable onCancel, final boolean newArrangement) {
		super(frame, Label.ARRANGEMENT_OPTIONS_PANE, 400);

		this.charterMenuBar = charterMenuBar;
		this.data = data;
		this.frame = frame;
		this.selectionManager = selectionManager;

		final Arrangement arrangement = data.currentArrangement();
		arrangementType = arrangement.arrangementType;
		arrangementSubtype = arrangement.arrangementSubtype;
		startingTone = arrangement.startingTone;
		tuning = new Tuning(arrangement.tuning);
		centOffset = arrangement.centOffset;
		capo = arrangement.capo;
		pickedBass = arrangement.pickedBass;

		final AtomicInteger row = new AtomicInteger(0);
		addArrangmentType(row);
		addArrangmentSubtype(row);

		addStringConfigValue(row.getAndIncrement(), 20, 0, Label.STARTING_TONE, startingTone, 100,
				this::validateBaseTone, val -> startingTone = val, false);
		addStringConfigValue(row.get(), 20, 0, Label.TUNING_PITCH,
				formatPitch(AudioUtils.centsToPitch(440, centOffset.doubleValue())), 100, this::validateTuningPitch,
				this::setTuningPitch, false);

		centOffsetLabel = new JLabel("", JLabel.LEFT);
		setCentsValue();
		add(centOffsetLabel, 200, getY(row.getAndIncrement()), 150, 20);

		row.incrementAndGet();
		addTuningSelect(row);
		addStringsCapo(row);
		addPickedBass(row);

		row.incrementAndGet();
		tuningInputsRow = row.getAndAdd(2);
		addTuningInputsAndLabels();
		setTuningValuesAndLabels();

		if (newArrangement) {
			moveFrets = null;
		} else {
			addMoveFretsCheckbox(row);
		}

		setOnFinish(this::saveAndExit, onCancel);
		addDefaultFinish(row.incrementAndGet());
	}

	private String validateBaseTone(final String text) {
		if (text == null || text.isEmpty()) {
			return Label.VALUE_CANT_BE_EMPTY.label();
		}

		return null;
	}

	private String validateTuningPitch(final String text) {
		if (text == null || text.isBlank()) {
			return Label.VALUE_CANT_BE_EMPTY.label();
		}
		Double pitch;
		try {
			pitch = Double.valueOf(text.replace(',', '.'));
		} catch (final NumberFormatException e) {
			return Label.VALUE_NUMBER_EXPECTED.label();
		}
		if (pitch <= 0) {
			return Label.PITCH_MUST_BE_POSITIVE.label();
		}

		return null;
	}

	private String formatPitch(final double pitch) {
		return "%.2f".formatted(pitch).replace(",", ".");
	}

	private void setCentsValue() {
		final String text = Label.CENTS.format("%+.2f".formatted(centOffset).replace(",", "."));
		centOffsetLabel.setText(text);
	}

	private void setTuningPitch(final String pitch) {
		final double tuningPitch = Double.valueOf(pitch);
		final double cents = AudioUtils.pitchToCents(440, tuningPitch);
		centOffset = new BigDecimal(cents).setScale(2, RoundingMode.HALF_UP);
		setCentsValue();
	}

	private void setArrangementType(final ArrangementType newArrangementType) {
		final ArrangementType oldArrangementType = arrangementType;
		arrangementType = newArrangementType;

		pickedBassField.setVisible(arrangementType == ArrangementType.Bass);
		pickedBassField.repaint();
		if (oldArrangementType != ArrangementType.Bass && arrangementType == ArrangementType.Bass) {
			stringsInput.setText("4");
		} else if (oldArrangementType == ArrangementType.Bass && arrangementType != ArrangementType.Bass) {
			stringsInput.setText("6");
		}

		setTuningLabels();
	}

	private void addArrangmentType(final AtomicInteger row) {
		final CharterSelect<ArrangementType> input = new CharterSelect<>(ArrangementType.values(), arrangementType,
				v -> v.name(), this::setArrangementType);

		addLabel(row.get(), 20, Label.ARRANGEMENT_OPTIONS_TYPE, 0);
		add(input, 150, getY(row.getAndIncrement()), 100, 20);
	}

	private void setArrangementSubtype(final ArrangementSubtype newArrangementSubtype) {
		arrangementSubtype = newArrangementSubtype;
	}

	private void addArrangmentSubtype(final AtomicInteger row) {
		final CharterSelect<ArrangementSubtype> input = new CharterSelect<>(ArrangementSubtype.values(),
				arrangementSubtype, v -> v.label.label(), this::setArrangementSubtype);

		addLabel(row.get(), 20, Label.ARRANGEMENT_OPTIONS_SUBTYPE, 0);
		add(input, 150, getY(row.getAndIncrement()), 100, 20);
	}

	private void addTuningSelect(final AtomicInteger row) {
		tuningSelect = new CharterSelect<>(TuningType.values(), tuning.tuningType, v -> v.name, this::onTuningSelected);

		addLabel(row.get(), 20, Label.ARRANGEMENT_OPTIONS_TUNING_TYPE, 0);
		this.add(tuningSelect, 75, getY(row.getAndIncrement()), 200, 20);
	}

	private void addStringsCapo(final AtomicInteger row) {
		addIntegerConfigValue(row.get(), 20, 0, Label.ARRANGEMENT_OPTIONS_STRINGS, tuning.strings(), 20,
				new IntegerValueValidator(1, InstrumentConfig.maxStrings, false), //
				this::onTuningStringsChanged, false);
		stringsInput = (TextInputWithValidation) getPart(-1);
		stringsInput.setHorizontalAlignment(JTextField.CENTER);
		addSelectTextOnFocus(stringsInput);

		addIntegerConfigValue(row.get(), 120, 0, Label.ARRANGEMENT_OPTIONS_CAPO, capo, 30,
				new IntegerValueValidator(0, InstrumentConfig.frets, false), //
				val -> capo = val, //
				false);
		final TextInputWithValidation capoInput = (TextInputWithValidation) getPart(-1);
		capoInput.setHorizontalAlignment(JTextField.CENTER);
		addSelectTextOnFocus(capoInput);
	}

	private void addPickedBass(final AtomicInteger row) {
		final JCheckBox checkbox = new JCheckBox();
		checkbox.setSelected(pickedBass);
		checkbox.addActionListener(e -> pickedBass = checkbox.isSelected());

		pickedBassField = new FieldWithLabel<>(Label.PICKED_BASS, 80, 20, 20, checkbox, LabelPosition.LEFT_CLOSE);
		pickedBassField.setVisible(arrangementType == ArrangementType.Bass);
		add(pickedBassField, 170, getY(row.get()), pickedBassField.getWidth(), pickedBassField.getHeight());
	}

	private void addTuningInputsAndLabels() {
		final int inputWidth = 30;
		for (int i = 0; i < InstrumentConfig.maxStrings; i++) {
			final int string = i;
			final int x = 20 + i * 40;

			addIntegerConfigValue(tuningInputsRow, x, 0, null, 0, inputWidth, //
					new IntegerValueValidator(-48, 48, false), //
					val -> onTuningValueChanged(string, val), //
					false);
			final TextInputWithValidation tuningInput = (TextInputWithValidation) getPart(-1);
			tuningInput.setHorizontalAlignment(JTextField.CENTER);
			addSelectTextOnFocus(tuningInput);
			tuningInputs.add(tuningInput);

			final JLabel label = new JLabel("", JLabel.CENTER);
			add(label, x, getY(tuningInputsRow + 1), inputWidth, 20);

			tuningLabels.add(label);

			if (i >= tuning.strings()) {
				this.remove(tuningInput);
				this.remove(label);
			}
		}
	}

	private void setTuningValuesAndLabels() {
		final boolean bass = arrangementType.isBass(tuning.strings());
		final int[] tuningValues = tuning.getTuning();
		for (int string = 0; string < tuningValues.length; string++) {
			final int value = tuningValues[string];
			final int distanceFromC0 = value + getStringDistanceFromC0(string, tuning.strings(), bass);
			final String name = soundToFullName(distanceFromC0, true);

			tuningInputs.get(string).setTextWithoutEvent("" + value);
			tuningLabels.get(string).setText(name);
		}
	}

	private void setTuningLabels() {
		final boolean bass = arrangementType.isBass(tuning.strings());
		final int[] tuningValues = tuning.getTuning();
		for (int string = 0; string < tuningValues.length; string++) {
			final int distanceFromC0 = tuningValues[string] + getStringDistanceFromC0(string, tuning.strings(), bass);

			tuningLabels.get(string).setText(soundToFullName(distanceFromC0, true));
		}
	}

	private void addMoveFretsCheckbox(final AtomicInteger row) {
		final JCheckBox checkbox = new JCheckBox();
		checkbox.setSelected(true);

		moveFrets = new FieldWithLabel<>(Label.ARRANGEMENT_OPTIONS_MOVE_FRETS, 5, 20, 20, checkbox,
				LabelPosition.RIGHT_PACKED);
		add(moveFrets, 20, getY(row.getAndIncrement()), 200, 20);
	}

	private void onTuningSelected(final TuningType newTuningType) {
		if (ignoreEvents) {
			return;
		}

		ignoreEvents = true;
		tuning = new Tuning(newTuningType, tuning.strings());

		setTuningValuesAndLabels();

		ignoreEvents = false;
	}

	private void onTuningStringsChanged(final int newStrings) {
		if (ignoreEvents) {
			return;
		}

		ignoreEvents = true;
		final int oldStrings = tuning.strings();
		tuning.strings(newStrings);

		for (int i = oldStrings; i < newStrings; i++) {
			this.add(tuningInputs.get(i), 20 + i * 40, getY(tuningInputsRow), 30, 20);
			this.add(tuningLabels.get(i), 20 + i * 40, getY(tuningInputsRow + 1), 30, 20);
		}

		for (int i = newStrings; i < oldStrings; i++) {
			this.remove(tuningInputs.get(i));
			this.remove(tuningLabels.get(i));
		}
		setTuningValuesAndLabels();

		repaint();

		ignoreEvents = false;
	}

	private void onTuningValueChanged(final int string, final int newValue) {
		if (ignoreEvents) {
			return;
		}

		ignoreEvents = true;

		tuning.changeTuning(string, newValue);
		tuningSelect.setSelectedIndex(tuning.tuningType.ordinal());
		final boolean bass = arrangementType.isBass(tuning.strings());
		final int distanceFromC0 = newValue + getStringDistanceFromC0(string, tuning.strings(), bass);
		tuningLabels.get(string).setText(soundToFullName(distanceFromC0, newValue > 0));

		ignoreEvents = false;
	}

	private void changeChordTemplate(final ChordTemplate chordTemplate, final int[] fretsDifference) {
		boolean templateChanged = false;
		for (final int string : new ArrayList<>(chordTemplate.frets.keySet())) {
			if (string >= tuning.strings()) {
				chordTemplate.frets.remove(string);
				chordTemplate.fingers.remove(string);
			} else if (fretsDifference[string] != 0) {
				templateChanged = true;
				int newFret = chordTemplate.frets.get(string) + fretsDifference[string];
				if (newFret < 0) {
					newFret = 0;
				}
				chordTemplate.frets.put(string, newFret);
			}
		}

		if (templateChanged) {
			setSuggestedFingers(chordTemplate);
		}
	}

	private void saveAndExit() {
		final Arrangement arrangement = data.currentArrangement();

		if (moveFrets != null && moveFrets.field.isSelected()) {
			final int[] fretsDifference = new int[tuning.strings()];
			final int[] tuningBefore = arrangement.tuning.getTuning();
			final int[] tuningAfter = tuning.getTuning();
			for (int string = 0; string < tuning.strings(); string++) {
				fretsDifference[string] = arrangement.tuning.strings() <= string ? 0
						: tuningBefore[string] - tuningAfter[string];
			}

			arrangement.chordTemplates.forEach(chordTemplate -> changeChordTemplate(chordTemplate, fretsDifference));

			arrangement.levels.forEach(level -> {
				level.sounds.forEach(sound -> {
					if (!sound.isNote()) {
						return;
					}

					if (sound.note().string >= tuning.strings()) {
						sound.note().string = tuning.strings() - 1;
					} else {
						sound.note().fret = max(0, sound.note().fret + fretsDifference[sound.note().string]);
					}
				});
			});
		}

		arrangement.arrangementType = arrangementType;
		arrangement.arrangementSubtype = arrangementSubtype;
		arrangement.startingTone = startingTone;
		arrangement.centOffset = centOffset;
		arrangement.tuning = tuning;
		arrangement.capo = capo;
		arrangement.pickedBass = pickedBass;

		selectionManager.clear();

		charterMenuBar.refreshMenus();
		frame.updateSizes();
	}
}
