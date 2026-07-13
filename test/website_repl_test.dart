import 'package:flutter_test/flutter_test.dart';

import 'package:clj_br_flutter_web/cljd-out/clj-br/website.dart' as website;
import 'package:clj_br_flutter_web/cljd-out/sci/core.dart' as sci;

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  dynamic evaluate(dynamic context, String source) =>
      sci.eval_string$STAR_(context, source);

  testWidgets('ClojureDart Site sample defines open-url! in the REPL', (
    tester,
  ) async {
    final context = website.build_sci_env();

    evaluate(context, website.site_cljd_source$v1 as String);

    expect(evaluate(context, '(fn? open-url!)'), isTrue);
  });

  testWidgets('ClojureDart Counter sample evaluates', (tester) async {
    final context = website.build_sci_env();

    expect(
      () => evaluate(context, website.counter_cljd_source$v1 as String),
      returnsNormally,
    );
    expect(evaluate(context, '(swap! counter inc)'), 1);
  });

  testWidgets('ClojureDart Todo sample evaluates', (tester) async {
    final context = website.build_sci_env();

    expect(
      () => evaluate(context, website.todo_cljd_source$v1 as String),
      returnsNormally,
    );
    evaluate(context, '(reset! draft "Testar o REPL") (add-todo!)');
    expect(evaluate(context, '(count @todos)'), 4);
    expect(evaluate(context, '@draft'), isEmpty);
  });
}
