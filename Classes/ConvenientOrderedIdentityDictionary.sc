ConvenientOrderedIdentityDictionary : IdentityDictionary {
	var <order;

	*newFromAssociationArray{arg arr, recursive = true;
		var result = this.new;
		if(recursive, {
			if(arr.isTrulyAssociationArray, {
				arr.do({arg assoc;
					var val = assoc.value;
					if(val.isKindOf(SequenceableCollection) and: {val.isTrulyAssociationArray}, {
						val = this.newFromAssociationArray(assoc.value);
					}, {
						val = assoc.value;
					});
					result.put(assoc.key, val);
				});
			});
		}, {
			if(arr.isTrulyAssociationArray, {
				arr.do({arg assoc;
					result.put(assoc.key, assoc.value);
				});
			});
		});
		^result;
	}

	*newFromNestedAssociationsArray{|arr|
		var result = this.new;
		var assocList = List.new;
		var addNodes;
		addNodes = {|key, value, res|
			if(key.isKindOf(Symbol), {
				if(value.isKindOf(ArrayedCollection) and: {value.isKindOf(String).not}, {
					if(value.every({|it| it.isKindOf(Association)}), {
						value.do({|item|
							if(item.isKindOf(Association), {
								var k, v;
								k = "%/%".format(key, item.key).asSymbol;
								v = item.value;
								addNodes.value(k, v, res);
							});
						});
					}, {
						res = res.add(key -> value.copy);
					});
				}, {
					res = res.add(key -> value.copy);
				});
			}, {
				if(key.isKindOf(ArrayedCollection), {
					key.do({|k|
						addNodes.value(k, value, res);
					});
				});
			});
		};
		arr.do({|it|
			addNodes.value(it.key, it.value, assocList);
		});
		assocList.do({|item|
			result.put(item.key, item.value);
		});
		^result;
	}

	put{| key, value |
		if(this.includesKey(key).not, {
			order = order.add(key);
		});
		^super.put(key, value);
	}

	keysValuesArrayDo { | argArray, function |
		var arr;
		if(this.isEmpty.not, {
			arr = [
				order,
				order.collect({| item | this.at(item); })
			].lace;
			super.keysValuesArrayDo(arr, function);
		});
	}

	keys { | species(Array) |
		^super.keys(species);
	}

	values {
		var list = List.new(size);
		this.do({ | value | list.add(value) });
		^list
	}

	sorted{
		var result = this.class.new(size);
		order.sort.do({| item |
			result.put(item, this.at(item));
		});
		^result;
	}

	//adding additional check for equal order
	== {| what |
		var result = super == what;
		if(result.not, { ^false; });
		if(order != what.order, {^false;});
		^true;
	}

	removeAt{| key |
		if(order.includes(key), {
			order.remove(key);
		});
		^super.removeAt(key);
	}

	first{
		^this.at(this.order.first);
	}

	//If the dictionary is a multi level dictionary method flattens
	//the key to forward slash separated paths:
	//e.g.
	//VTMOrderedIdentityDictionary[
	//	\aaa -> (v: 11),
	//	\bbb -> [
	//		\xxx -> (v:777),
	//		\yyy -> (v:888),
	//		\zzz -> (v:999)
	//	],
	//	[\eee, \fff] -> (v: 1234),
	//	[\hhh, \iii, \jjj] -> [
	//		\aa -> (v:1111),
	//		\bb -> (v:2222),
	//	],
	//	\xxx -> 1,
	//	\yyy -> "hello"
	//];
	//
	//becomes:
	// (aaa -> ( 'v': 11 ))
	// (bbb/xxx -> ( 'v': 777 ))
	// (bbb/yyy -> ( 'v': 888 ))
	// (bbb/zzz -> ( 'v': 999 ))
	// (eee -> ( 'v': 1234 ))
	// (fff -> ( 'v': 1234 ))
	// (hhh/aa -> ( 'v': 1111 ))
	// (hhh/bb -> ( 'v': 2222 ))
	// (iii/aa -> ( 'v': 1111 ))
	// (iii/bb -> ( 'v': 2222 ))
	// (jjj/aa -> ( 'v': 1111 ))
	// (jjj/bb -> ( 'v': 2222 ))
	// (xxx -> 1)
	// (yyy -> "hello")
	flattenedNestedKeys{
		var addNodes;
		var result = List.new;
		addNodes = {|key, value, res|
			if(key.isKindOf(Symbol), {
				if(value.isKindOf(ArrayedCollection) and: {value.isKindOf(String).not}, {
					if(value.every({|it| it.isKindOf(Association)}), {
						value.do({|item|
							if(item.isKindOf(Association), {
								var k, v;
								k = "%/%".format(key, item.key).asSymbol;
								v = item.value;
								addNodes.value(k, v, res);
							});
						});
					}, {
						res = res.add(key -> value);
					});
				}, {
					res = res.add(key -> value);
				});
			}, {
				if(key.isKindOf(ArrayedCollection), {
					key.do({|k|
						addNodes.value(k, value, res);
					});
				});
			});
		};
		this.keysValuesDo({|k,v|
			addNodes.value(k,v,result);
		});
		^result;
	}
}
