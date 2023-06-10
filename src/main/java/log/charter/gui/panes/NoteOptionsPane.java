package log.charter.gui.panes;

import static java.util.Arrays.asList;
import static log.charter.gui.components.TextInputSelectAllOnFocus.addSelectTextOnFocus;
import static log.charter.gui.components.TextInputWithValidation.ValueValidator.createIntValidator;

import javax.swing.JTextField;

import log.charter.data.ChartData;
import log.charter.data.config.Config;
import log.charter.data.config.Localization.Label;
import log.charter.data.undoSystem.UndoSystem;
import log.charter.gui.CharterFrame;
import log.charter.gui.components.ParamsPane;
import log.charter.song.ChordTemplate;
import log.charter.song.enums.BassPickingTechnique;
import log.charter.song.enums.HOPO;
import log.charter.song.enums.Harmonic;
import log.charter.song.enums.Mute;
import log.charter.song.notes.Chord;
import log.charter.song.notes.ChordOrNote;
import log.charter.song.notes.Note;
import log.charter.util.CollectionUtils.ArrayList2;
import log.charter.util.CollectionUtils.Pair;

public class NoteOptionsPane extends ParamsPane {
	private static final long serialVersionUID = 1L;

	private static PaneSizes getSizes() {
		final PaneSizes sizes = new PaneSizes();
		sizes.labelWidth = 80;
		sizes.width = 300;
		sizes.rowHeight = 20;

		return sizes;
	}

	private final UndoSystem undoSystem;

	private final ArrayList2<ChordOrNote> chordsAndNotes;

	private int string;
	private int fret;
	private Mute mute;
	private HOPO hopo;
	private BassPickingTechnique bassPicking;
	private Harmonic harmonic;
	private boolean accent;
	private boolean linkNext;
	private boolean ignore;
	private Integer slideTo;
	private boolean unpitchedSlide;
	private boolean vibrato;
	private boolean tremolo;

	public NoteOptionsPane(final ChartData data, final CharterFrame frame, final UndoSystem undoSystem,
			final ArrayList2<ChordOrNote> notes) {
		super(frame, Label.NOTE_OPTIONS_PANE, 21, getSizes());
		this.undoSystem = undoSystem;

		chordsAndNotes = notes;

		final ChordOrNote chordOrNote = notes.get(0);
		if (chordOrNote.note != null) {
			getNoteValues(chordOrNote.note);
		} else {
			final Chord chord = chordOrNote.chord;
			final ChordTemplate chordTemplate = data.getCurrentArrangement().chordTemplates.get(chord.chordId);
			getChordValues(chord, chordTemplate);
		}

		addInputs(data.getCurrentArrangement().tuning.strings);
	}

	private void addInputs(final int strings) {
		int row = 0;
		addIntegerConfigValue(row, 20, 0, Label.STRING, string + 1, 30, createIntValidator(1, strings, false),
				val -> string = val - 1, false);
		final JTextField stringInput = (JTextField) components.getLast();
		stringInput.setHorizontalAlignment(JTextField.CENTER);
		addSelectTextOnFocus(stringInput);

		addIntegerConfigValue(row++, 100, 0, Label.FRET, fret, 30, createIntValidator(0, Config.frets, false),
				val -> fret = val, false);
		final JTextField fretInput = (JTextField) components.getLast();
		fretInput.setHorizontalAlignment(JTextField.CENTER);
		addSelectTextOnFocus(fretInput);

		final int radioButtonWidth = 65;

		row++;
		final int muteLabelY = getY(row++);
		addLabelExact(muteLabelY, 20, Label.MUTE);
		final int muteRadioY = getY(row++) - 3;
		addConfigRadioButtonsExact(muteRadioY, 30, radioButtonWidth, mute, val -> mute = val, //
				asList(new Pair<>(Mute.FULL, Label.MUTE_FULL), //
						new Pair<>(Mute.PALM, Label.MUTE_PALM), //
						new Pair<>(Mute.NONE, Label.MUTE_NONE)));

		final int hopoLabelY = getY(row++) + 3;
		addLabelExact(hopoLabelY, 20, Label.HOPO);
		final int hopoRadioY = getY(row++);
		addConfigRadioButtonsExact(hopoRadioY, 30, radioButtonWidth, hopo, val -> hopo = val, //
				asList(new Pair<>(HOPO.HAMMER_ON, Label.HOPO_HAMMER_ON), //
						new Pair<>(HOPO.PULL_OFF, Label.HOPO_PULL_OFF), //
						new Pair<>(HOPO.TAP, Label.HOPO_TAP), //
						new Pair<>(HOPO.NONE, Label.HOPO_NONE)));

		final int bassPickingLabelY = getY(row++) + 6;
		addLabelExact(bassPickingLabelY, 20, Label.BASS_PICKING_TECHNIQUE);
		final int bassPickingRadioY = getY(row++) + 3;
		addConfigRadioButtonsExact(bassPickingRadioY, 30, 60, bassPicking, val -> bassPicking = val, //
				asList(new Pair<>(BassPickingTechnique.POP, Label.BASS_PICKING_POP), //
						new Pair<>(BassPickingTechnique.SLAP, Label.BASS_PICKING_SLAP), //
						new Pair<>(BassPickingTechnique.NONE, Label.BASS_PICKING_NONE)));

		final int harmonicLabelY = getY(row++) + 9;
		addLabelExact(harmonicLabelY, 20, Label.HARMONIC);
		final int harmonicRadioY = getY(row++) + 6;
		addConfigRadioButtonsExact(harmonicRadioY, 30, radioButtonWidth, harmonic, val -> harmonic = val, //
				asList(new Pair<>(Harmonic.NORMAL, Label.HARMONIC_NORMAL), //
						new Pair<>(Harmonic.PINCH, Label.HARMONIC_PINCH), //
						new Pair<>(Harmonic.NONE, Label.HARMONIC_NONE)));

		row++;
		addConfigCheckbox(row, 20, 45, Label.ACCENT, accent, val -> accent = val);
		addConfigCheckbox(row, 110, 0, Label.LINK_NEXT, linkNext, val -> linkNext = val);
		addConfigCheckbox(row++, 210, 0, Label.IGNORE_NOTE, ignore, val -> ignore = val);

		addIntegerConfigValue(row, 20, 45, Label.SLIDE_PANE_FRET, slideTo, 40,
				createIntValidator(1, Config.frets, true), val -> slideTo = val, false);
		final JTextField slideToInput = (JTextField) components.getLast();
		slideToInput.setHorizontalAlignment(JTextField.CENTER);
		addSelectTextOnFocus(slideToInput);
		addConfigCheckbox(row++, 120, 0, Label.SLIDE_PANE_UNPITCHED, unpitchedSlide, val -> unpitchedSlide = val);

		addConfigCheckbox(row++, 20, 45, Label.VIBRATO, vibrato, val -> vibrato = val);
		addConfigCheckbox(row++, 20, 45, Label.TREMOLO, tremolo, val -> tremolo = val);

		addDefaultFinish(16, this::onSave);
	}

	private void getNoteValues(final Note note) {
		string = note.string;
		fret = note.fret;
		mute = note.mute;
		hopo = note.hopo;
		bassPicking = note.bassPicking;
		harmonic = note.harmonic;
		accent = note.accent;
		linkNext = note.linkNext;
		ignore = note.ignore;
		slideTo = note.slideTo;
		unpitchedSlide = note.unpitchedSlide;
		vibrato = note.vibrato;
		tremolo = note.tremolo;
	}

	private void getChordValues(final Chord chord, final ChordTemplate chordTemplate) {
		string = chordTemplate.getStringRange().min;
		fret = chordTemplate.frets.get(string);
		mute = chord.mute;
		hopo = HOPO.NONE;
		bassPicking = BassPickingTechnique.NONE;
		harmonic = Harmonic.NONE;
		accent = chord.accent;
		linkNext = chord.linkNext;
		ignore = chord.ignore;
		slideTo = chord.slideTo;
		unpitchedSlide = chord.unpitchedSlide;
		vibrato = chord.vibrato;
		tremolo = chord.tremolo;
	}

	private void setNoteValues(final Note note) {
		note.string = string;
		note.fret = fret;
		note.mute = mute;
		note.hopo = hopo;
		note.bassPicking = bassPicking;
		note.harmonic = harmonic;
		note.slideTo = slideTo;
		note.unpitchedSlide = unpitchedSlide;
		note.accent = accent;
		note.linkNext = linkNext;
		note.ignore = ignore;
		note.vibrato = vibrato;
		note.tremolo = tremolo;
	}

	private void changeChordToNote(final ChordOrNote chordOrNote) {
		final Note note = new Note(chordOrNote.position(), string, fret);
		note.mute = mute;
		note.hopo = hopo;
		note.bassPicking = bassPicking;
		note.harmonic = harmonic;
		note.slideTo = slideTo;
		note.unpitchedSlide = unpitchedSlide;
		note.accent = accent;
		note.linkNext = linkNext;
		note.ignore = ignore;
		note.vibrato = vibrato;
		note.tremolo = tremolo;
		chordOrNote.note = note;
		chordOrNote.chord = null;
	}

	private void onSave() {
		undoSystem.addUndo();

		for (final ChordOrNote chordOrNote : chordsAndNotes) {
			if (chordOrNote.isChord()) {
				changeChordToNote(chordOrNote);
			} else {
				setNoteValues(chordOrNote.note);
			}
		}
	}

}
