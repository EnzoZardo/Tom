import { commands, window } from 'vscode';

function activate(context) {
  const disposable = commands.registerCommand('tomRunner.executar', () => {
    const editor = window.activeTextEditor;
    if (!editor) return;

    editor.document.save();
    const filePath = editor.document.fileName;

    const terminal = window.terminals.find(t => t.name === 'Tom Runner')
      ?? window.createTerminal('Tom Runner');

    terminal.show();
    terminal.sendText(`java -cp "/home/enzozardo/repo/Tom/out/production/Tom" MainClass "${filePath}"`);
  });

  context.subscriptions.push(disposable);
}

export default { activate };