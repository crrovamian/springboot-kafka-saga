return {
	"NickvanDyke/opencode.nvim",
	dependencies = {
		{
			"folke/snacks.nvim",
			opts = {
				input = {},
				picker = {},
				terminal = {},
			},
		},
	},
	keys = {
		-- Toggle: ahora va por snacks.terminal directamente
		{
			"<leader>aa",
			function()
				local cmd = "opencode --port"
				local opts = { win = { position = "right", enter = false } }
				require("snacks.terminal").toggle(cmd, opts)
			end,
			mode = { "n", "t" },
			desc = "Toggle OpenCode",
		},
		{
			"<leader>as",
			function()
				require("opencode").select()
			end,
			mode = { "n", "x" },
			desc = "OpenCode select",
		},
		{
			"<leader>ai",
			function()
				require("opencode").ask("", { submit = false })
			end,
			mode = { "n", "x" },
			desc = "OpenCode ask",
		},
		{
			"<leader>aI",
			function()
				require("opencode").ask("@this: ", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode ask with context",
		},
		{
			"<leader>ab",
			function()
				require("opencode").ask("@buffer ", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode ask about buffer",
		},
		{
			"<leader>ap",
			function()
				require("opencode").prompt("@this", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode prompt",
		},
		-- Built-in prompts
		{
			"<leader>ape",
			function()
				require("opencode").prompt("explain", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode explain",
		},
		{
			"<leader>apf",
			function()
				require("opencode").prompt("fix", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode fix",
		},
		{
			"<leader>apd",
			function()
				require("opencode").prompt("diagnostics", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode diagnose",
		},
		{
			"<leader>apr",
			function()
				require("opencode").prompt("review", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode review",
		},
		{
			"<leader>apt",
			function()
				require("opencode").prompt("test", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode test",
		},
		{
			"<leader>apo",
			function()
				require("opencode").prompt("optimize", { submit = true })
			end,
			mode = { "n", "x" },
			desc = "OpenCode optimize",
		},
	},
	config = function()
		local cmd = "opencode --port"
		local win_opts = {
			win = {
				position = "right",
				enter = false, -- no roba el foco al abrir
				on_win = function(win)
					require("opencode.terminal").setup(win.win)
				end,
			},
		}

		vim.g.opencode_opts = {
			server = {
				start = function()
					require("snacks.terminal").open(cmd, win_opts)
				end,
				stop = function()
					local t = require("snacks.terminal").get(cmd, win_opts)
					if t then
						t:close()
					end
				end,
				toggle = function()
					require("snacks.terminal").toggle(cmd, win_opts)
				end,
			},
		}

		vim.o.autoread = true
	end,
}
