

~supportedExtensions.includes(PathName("A:\LYDFILER\Drums\Basement Drums\Samples\._BG mf Snare1 Top 01.wav").extension.toLower.asSymbol)
~supportedExtensions = #[\wav, \wave, \aif, \aiff, \flac];
~folder = PathName("A:/LYDFILER/Drums/Basement Drums/Samples");

(
~files = ~folder.entries.select { | file |
	var hiddenFile;
	var result;

	file.fileName.do{ | char, i | if(char.isPunct and: i == 0, {"found a hidden file - avoiding".postln; hiddenFile = true})};

	if(hiddenFile.asBoolean.not, {
		result = ~supportedExtensions.includes(file.extension.toLower.asSymbol);
	}, {result = false});
	result;
};
)
~files.do{|file, i|  if(i<6, {file.postln})}


