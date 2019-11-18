// ConvenientGetter : FilterPattern {
// 	var >key, <result;
//
// 	*new { arg pattern, key;
// 		^super.newCopyArgs(pattern, key)
// 	}
//
// 	embedInStream { arg inval;
// 		var func, collected;
//
// 		if(key.isNil) {
// 			//collected = pattern.collect {|item| printStream << prefix << item << Char.nl; item }
// 			"no key was set in conv getter".error;
// 		} {
// 			func = { |val, item|
// 				if(val.isKindOf(Function) and: { item.isKindOf(Environment) })
// 				{
// 					val = item.use { val.value };
// 					printStream << prefix << val << "\t(printed function value)\n";
// 				} {
// 					printStream << prefix << val << Char.nl;
// 				};
// 			}.flop;
// 			collected = pattern.collect {|item|
// 				var val = item.atAll(key.asArray).unbubble;
// 				func.value(val, item, prefix);
// 				item
// 			}
// 		};
// 		^collected.embedInStream(inval)
// 	}
// }