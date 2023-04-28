+ C {

  *prKeyify { | input |
    var result;
    if (input == "r" or: {input == "rest"}) { input = input[0].toUpper++input[1..] };
    result = input
    .replace(($ ),"_")
    .replace(($-),"_")
    .replace(($,),"")
    .replace(($+),"")
    .replace("æ","ae")
    .replace("ø","o")
    .replace("å","aa")
    .asSymbol;
    ^result
  }

  // clean out windows seperator
  *prRemoveWindowsSeparator { | filepath |
      ^filepath.tr(Platform.pathSeparator , $/);
  }

  // *doesNotUnderstand { | |

  // }
  // *ptrPropsAsDictionaty { | identityset |
  //     ^identityset.collectAs({|assoc| assoc.key -> assoc.value}, Dictionary)
  // }

}
