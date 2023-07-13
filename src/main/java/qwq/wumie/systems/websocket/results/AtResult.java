package qwq.wumie.systems.websocket.results;

import qwq.wumie.systems.commands.commands.Check.GsonUtils;
import qwq.wumie.systems.websocket.Result;

public class AtResult extends Result {
    @Override
    public String toJSON() {
        return GsonUtils.beanToJson(get());
    }

    @Override
    public Result get() {
        return this;
    }
}
