-- plugins/toggleterm.lua
return {
	"akinsho/toggleterm.nvim",
	version = "*",
	opts = {
		open_mapping = [[<c-t>]],
		direction = "float",
		shell = "bash", -- ← esto arregla el sh
		float_opts = { border = "curved" },
		auto_scroll = true,
	},

	-- Múltiples Terminales:
	-- Puedes abrir varias ventanas y alternar entre ellas usando un prefijo numérico
	-- (ej. 2<c-t> para la terminal 2).
}
