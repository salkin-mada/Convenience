+ SequenceableCollection {
	isTrulyAssociationArray{
		^this.every({arg item; item.isKindOf(Association)});
	}
}
