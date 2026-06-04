local mode = {
	"mode",
	fmt = function(s)
		local mode_map = {
			["NORMAL"] = "N",
			["O-PENDING"] = "N?",
			["INSERT"] = "I",
			["VISUAL"] = "V",
			["V-BLOCK"] = "VB",
			["V-LINE"] = "VL",
			["V-REPLACE"] = "VR",
			["REPLACE"] = "R",
			["COMMAND"] = "!",
			["SHELL"] = "SH",
			["TERMINAL"] = "T",
			["EX"] = "X",
			["S-BLOCK"] = "SB",
			["S-LINE"] = "SL",
			["SELECT"] = "S",
			["CONFIRM"] = "Y?",
			["MORE"] = "M",
		}
		return mode_map[s] or s
	end,
}

local function codecompanion_adapter_name()
	local chat = require("codecompanion").buf_get_chat(vim.api.nvim_get_current_buf())
	if not chat then
		return nil
	end

	return " " .. chat.adapter.formatted_name
end

local function codecompanion_current_model_name()
	local chat = require("codecompanion").buf_get_chat(vim.api.nvim_get_current_buf())
	if not chat then
		return nil
	end

	return chat.settings.model
end

-- Transparencia al inicio
vim.schedule(function()
	-- vim.api.nvim_set_hl(0, "Normal", { bg = "NONE" })
	-- vim.api.nvim_set_hl(0, "NormalNC", { bg = "NONE" })
	-- vim.api.nvim_set_hl(0, "NormalFloat", { bg = "NONE" })
	-- vim.api.nvim_set_hl(0, "FloatBorder", { bg = "NONE" })

	local hl = vim.api.nvim_set_hl

	local transparent = { bg = "none" }

	-- UI base
	hl(0, "Normal", { fg = "#E0E0E0", bg = "none" })
	hl(0, "NormalNC", { fg = "#E0E0E0", bg = "none" })

	-- Floating windows (LazyVim + Telescope + LSP)
	hl(0, "NormalFloat", transparent)
	hl(0, "FloatBorder", { fg = "#6DB33F", bg = "none" })

	-- Menús
	hl(0, "Pmenu", transparent)
	hl(0, "PmenuSel", { bg = "#2E3B2E", fg = "#C5E1A5" })

	-- Sidebar (NvimTree / Snacks / etc.)
	hl(0, "SignColumn", transparent)
	hl(0, "EndOfBuffer", transparent)
	hl(0, "VertSplit", { fg = "#2E3B2E", bg = "none" })

	-- Identificadores y variables
	hl(0, "Identifier", { fg = "#E0E0E0" })
	hl(0, "Variable", { fg = "#E0E0E0" })
	hl(0, "Property", { fg = "#A5D6A7" })
	hl(0, "Parameter", { fg = "#C5E1A5" })

	-- Valores
	hl(0, "Constant", { fg = "#C5E1A5", bold = true })
	hl(0, "Boolean", { fg = "#8BC34A", bold = true })
	hl(0, "Number", { fg = "#A5D6A7" })

	-- Operadores y control
	hl(0, "Operator", { fg = "#6DB33F" })
	hl(0, "Conditional", { fg = "#6DB33F", bold = true })
	hl(0, "Repeat", { fg = "#6DB33F", bold = true })
	hl(0, "Statement", { fg = "#8BC34A" })

	-- Preprocesador (imports, includes, etc)
	hl(0, "PreProc", { fg = "#6A9955" })
	hl(0, "Include", { fg = "#6DB33F" })
	-- vim.cmd.colorscheme("tokyonight-night")

	-- Angular
	-- vim.api.nvim_set_hl(0, "Keyword", { fg = "#DD0031" })
	-- vim.api.nvim_set_hl(0, "Type", { fg = "#FF4D6D" })
	-- vim.api.nvim_set_hl(0, "Function", { fg = "#FF758F" })
	--
	-- hl = vim.api.nvim_set_hl

	-- Base UI
	-- hl(0, "Normal", { fg = "#E0E0E0", bg = "#1B1B1B" })
	-- hl(0, "Comment", { fg = "#6A9955", italic = true })

	-- Spring Boot vibe (verde + enterprise)
	-- hl(0, "Keyword", { fg = "#6DB33F", bold = true }) -- verde Spring
	-- hl(0, "Type", { fg = "#A5D6A7" })
	-- hl(0, "Function", { fg = "#8BC34A" })
	-- hl(0, "String", { fg = "#C5E1A5" })

	-- UI
	-- hl(0, "CursorLine", { bg = "#222222" })
	-- hl(0, "Visual", { bg = "#2E3B2E" })

	-- Spring
	-- vim.api.nvim_set_hl(0, "Keyword", { fg = "#6DB33F" })
	-- vim.api.nvim_set_hl(0, "Type", { fg = "#8BC34A" })
	-- vim.api.nvim_set_hl(0, "Function", { fg = "#A5D6A7" })
end)

-- Transparencia al cambiar de tema
vim.api.nvim_create_autocmd("ColorScheme", {
	pattern = "*",
	callback = function()
		vim.api.nvim_set_hl(0, "Normal", { bg = "NONE" })
		vim.api.nvim_set_hl(0, "NormalNC", { bg = "NONE" })
		vim.api.nvim_set_hl(0, "NormalFloat", { bg = "NONE" })
		vim.api.nvim_set_hl(0, "FloatBorder", { bg = "NONE" })
	end,
})

-- This file contains the configuration for various UI-related plugins in Neovim.
return {
	-- Plugin: folke/todo-comments.nvim
	-- URL: https://github.com/folke/todo-comments.nvim
	-- Description: Plugin to highlight and search for TODO, FIX, HACK, etc. comments in your code.
	-- IMPORTANT: using version "*" to fix a bug
	{ "folke/todo-comments.nvim", version = "*" },

	-- Plugin: folke/which-key.nvim
	-- URL: https://github.com/folke/which-key.nvim
	-- Description: Plugin to show a popup with available keybindings.
	-- IMPORTANT: using event "VeryLazy" to optimize loading time
	{
		"folke/which-key.nvim",
		event = "VeryLazy",
		opts = {
			preset = "classic",
			win = { border = "single" },
		},
	},

	-- Plugin: nvim-docs-view
	-- URL: https://github.com/amrbashir/nvim-docs-view
	-- Description: A Neovim plugin for viewing documentation.
	{
		"amrbashir/nvim-docs-view",
		lazy = true, -- Load this plugin lazily
		cmd = "DocsViewToggle", -- Command to toggle the documentation view
		opts = {
			position = "right", -- Position the documentation view on the right
			width = 60, -- Set the width of the documentation view
		},
	},

	-- Plugin: lualine.nvim
	-- URL: https://github.com/nvim-lualine/lualine.nvim
	-- Description: A blazing fast and easy to configure Neovim statusline plugin.
	{
		"nvim-lualine/lualine.nvim",
		event = "VeryLazy", -- Load this plugin on the 'VeryLazy' event
		requires = { "nvim-tree/nvim-web-devicons", opt = true }, -- Optional dependency for icons
		opts = {
			options = {
				-- theme = "gentleman-kanagawa-blur", -- Set the theme for lualine
				icons_enabled = true, -- Enable icons in the statusline
				theme = "auto",
				globalstatus = true,
				-- Añade esto:
				section_separators = "",
				component_separators = "",
			},
			sections = {
				lualine_a = {
					{
						"mode", -- Display the current mode
						icon = "󱗞", -- Set the icon for the mode
					},
				},
			},
			extensions = {
				"quickfix",
				{
					filetypes = { "oil" },
					sections = {
						lualine_a = {
							mode,
						},
						lualine_b = {
							function()
								local ok, oil = pcall(require, "oil")
								if not ok then
									return ""
								end

								---@diagnostic disable-next-line: param-type-mismatch
								local path = vim.fn.fnamemodify(oil.get_current_dir(), ":~")
								return path .. " %m"
							end,
						},
					},
				},
				{
					filetypes = { "codecompanion" },
					sections = {
						lualine_a = {
							mode,
						},
						lualine_b = {
							codecompanion_adapter_name,
						},
						lualine_c = {
							codecompanion_current_model_name,
						},
						lualine_x = {},
						lualine_y = {
							"progress",
						},
						lualine_z = {
							"location",
						},
					},
					inactive_sections = {
						lualine_a = {},
						lualine_b = {
							codecompanion_adapter_name,
						},
						lualine_c = {},
						lualine_x = {},
						lualine_y = {
							"progress",
						},
						lualine_z = {},
					},
				},
			},
		},
	},

	-- Plugin: incline.nvim
	-- URL: https://github.com/b0o/incline.nvim
	-- Description: A Neovim plugin for showing the current filename in a floating window.
	{
		"b0o/incline.nvim",
		event = "BufReadPre", -- Load this plugin before reading a buffer
		priority = 1200, -- Set the priority for loading this plugin
		config = function()
			require("incline").setup({
				window = { margin = { vertical = 0, horizontal = 1 } }, -- Set the window margin
				hide = {
					cursorline = true, -- Hide the incline window when the cursorline is active
				},
				render = function(props)
					local filename = vim.fn.fnamemodify(vim.api.nvim_buf_get_name(props.buf), ":t") -- Get the filename
					if vim.bo[props.buf].modified then
						filename = "[+] " .. filename -- Indicate if the file is modified
					end

					local icon, color = require("nvim-web-devicons").get_icon_color(filename) -- Get the icon and color for the file
					return { { icon, guifg = color }, { " " }, { filename } } -- Return the rendered content
				end,
			})
		end,
	},

	-- Plugin: zen-mode.nvim
	-- URL: https://github.com/folke/zen-mode.nvim
	-- Description: A Neovim plugin for distraction-free coding.
	{
		"folke/zen-mode.nvim",
		cmd = "ZenMode", -- Command to toggle Zen Mode
		opts = {
			plugins = {
				gitsigns = true, -- Enable gitsigns integration
				tmux = true, -- Enable tmux integration
				kitty = { enabled = false, font = "+2" }, -- Disable kitty integration and set font size
				twilight = { enabled = true }, -- Enable twilight integration
			},
		},
		keys = { { "<leader>z", "<cmd>ZenMode<cr>", desc = "Zen Mode" } }, -- Keybinding to toggle Zen Mode
	},

	-- Plugin: snacks.nvim
	-- URL: https://github.com/folke/snacks.nvim/tree/main
	-- Description: A Neovim plugin for creating a customizable dashboard.
	{
		"folke/snacks.nvim",
		keys = {
			{
				"<leader>fb",
				function()
					Snacks.picker.buffers()
				end,
				desc = "Find Buffers",
			},
		},
		opts = {
			styles = {
				notification = { wo = { winblend = 0 } },
			},
			notifier = {},
			image = {},
			picker = {
				exclude = {
					".git",
					"node_modules",
				},
				matcher = {
					fuzzy = true,
					smartcase = true,
					ignorecase = true,
					filename_bonus = true,
				},
				sources = {
					-- explorer = {
					--   matcher = {
					--     fuzzy = true, -- Enables fuzzy matching, so you can be a bit imprecise with your search terms
					--     smartcase = true, -- If your search term has uppercase letters, the search becomes case-sensitive
					--     ignorecase = true, -- Ignores case when searching, unless smartcase is triggered
					--     filename_bonus = true, -- Gives a higher priority to matches in filenames
					--     sort_empty = false, -- If no matches are found, it won't sort the results
					--   },
					-- },
				},
			},
			dashboard = {
				sections = {
					{ section = "header" },
					{ icon = " ", title = "Keymaps", section = "keys", indent = 2, padding = 1 },
					{ icon = " ", title = "Recent Files", section = "recent_files", indent = 2, padding = 1 },
					{ icon = " ", title = "Projects", section = "projects", indent = 2, padding = 1 },
					{ section = "startup" },
				},
				preset = {
					header = [[
             ++++++++++++++++++++++++             
          ++++++++++++++++++++++++++++++          
         ++++++++++++++++++++++++++++++++         
        ++++++++++++++++++++++++++++++++++        
       ++++++++++++++++++++++++++++++++++++       
      +++++++++++++++++   ++++++++++++++++++      
     ++++++++++++  ++++   +++++  ++++++++++++     
    +++++++++++   +++++   ++++++   +++++++++++    
   +++++++++++  +++++++   +++++++   +++++++++++   
  +++++++++++  ++++++++   +++++++++  +++++++++++  
 +++++++++++   ++++++++   +++++++++   +++++++++++ 
++++++++++++  +++++++++   +++++++++   ++++++++++++
++++++++++++  ++++++++++  +++++++++   ++++++++++++
++++++++++++   ++++++++++++++++++++   ++++++++++++
 +++++++++++   +++++++++++++++++++   ++++++++++++ 
  +++++++++++   +++++++++++++++++   ++++++++++++  
   ++++++++++++   ++++++++++++++   ++++++++++++   
    +++++++++++++    ++++++++    +++++++++++++    
     +++++++++++++++         ++++++++++++++++     
      ++++++++++++++++++++++++++++++++++++++      
       ++++++++++++++++++++++++++++++++++++       
        ++++++++++++++++++++++++++++++++++        
         ++++++++++++++++++++++++++++++++         
          ++++++++++++++++++++++++++++++          
            ++++++++++++++++++++++++++     
]],
          -- stylua: ignore
          ---@type snacks.dashboard.Item[]
          keys = {
            { icon = " ", key = "f", desc = "Find File", action = ":lua Snacks.dashboard.pick('files')" },
            { icon = " ", key = "n", desc = "New File", action = ":ene | startinsert" },
            { icon = " ", key = "g", desc = "Find Text", action = ":lua Snacks.dashboard.pick('live_grep')" },
            { icon = " ", key = "r", desc = "Recent Files", action = ":lua Snacks.dashboard.pick('oldfiles')" },
            { icon = " ", key = "c", desc = "Config", action = ":lua Snacks.dashboard.pick('files', {cwd = vim.fn.stdpath('config')})" },
            { icon = " ", key = "s", desc = "Restore Session", section = "session" },
            { icon = " ", key = "x", desc = "Lazy Extras", action = ":LazyExtras" },
            { icon = "󰒲 ", key = "l", desc = "Lazy", action = ":Lazy" },
            { icon = " ", key = "q", desc = "Quit", action = ":qa" },
          },
				},
			},
		},
	},
}
