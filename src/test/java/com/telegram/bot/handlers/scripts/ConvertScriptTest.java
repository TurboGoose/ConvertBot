package com.telegram.bot.handlers.scripts;

class ConvertScriptTest {
// update has no message -> nothing
    // message has text -> text is not stop word -> noting
    // to add images you need to call update with doc before

    // message has text -> text is stop word -> images are empty -> nothing
    // message has text -> text is stop word -> images are not empty -> upload file (execute)

    // message has doc -> wrong extension -> nothing
    // message has doc -> right extension -> max pics exceeded -> convert and send (execute)
    // message has doc -> right extension -> max pics not exceeded -> nothing

    // message has photo -> max pics exceeded -> convert and send (execute)
    // message has photo -> max pics not exceeded -> nothing
}