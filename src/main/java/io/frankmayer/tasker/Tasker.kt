package io.frankmayer.tasker

import io.frankmayer.tasker.config.Task
import io.frankmayer.tasker.config.TaskerConfigManager
import io.frankmayer.tasker.executor.TaskExecutor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class Tasker : JavaPlugin() {
    private var config: TaskerConfigManager? = null
    private var executor: TaskExecutor? = null

    override fun onEnable() {
        val dir = dataFolder
        if (!dir.exists()) {
            dir.mkdir()
        }

        val log = { level: Level, msg: String ->
            Bukkit.getScheduler().callSyncMethod(this) {
                Bukkit.getLogger().log(level, msg)
            }
        }

        val exec = { command: String ->
            Bukkit.getScheduler().callSyncMethod(this) {
                try {
                    server.dispatchCommand(
                        server.consoleSender,
                        command
                    )
                } catch (e: Exception) {
                    log(Level.ALL, "Task failed: '$command' Error: ${e.message}")
                }
            }
        }

        config = TaskerConfigManager(dir.absolutePath, Task("0 * * ? * *", "say Hello from Tasker!"))
        executor = TaskExecutor(config!!, exec, log)
    }

    override fun onDisable() {
        config?.exportConfig()
        executor?.shutdown()
    }
}
