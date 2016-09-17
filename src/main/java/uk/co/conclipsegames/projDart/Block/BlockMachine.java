package uk.co.conclipsegames.projDart.Block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import uk.co.conclipsegames.projDart.TileEntity.TileForceInfuser;

import java.util.List;

public class BlockMachine extends Block {

    public BlockMachine() {

        super(Material.iron);
        setHardness(15.0F);
        setResistance(25.0F);
        setBlockName("thermalexpansion.machine");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {

        if (metadata >= Types.values().length) {
            return null;
        }
        switch (Types.values()[metadata]) {
            case FORCEINFUSER:
                return new TileForceInfuser();
            default:
                return null;
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {

        for (int i = 0; i < Types.values().length; i++) {
            for (int j = 0; j < 4; j++) {
                if (creativeTiers[j]) {
                    list.add(ItemBlockMachine.setDefaultTag(new ItemStack(item, 1, i), (byte) j));
                }
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack) {

        if (stack.stackTagCompound != null) {
            TileMachineBase tile = (TileMachineBase) world.getTileEntity(x, y, z);

            tile.readAugmentsFromNBT(stack.stackTagCompound);
            tile.installAugments();
            tile.setEnergyStored(stack.stackTagCompound.getInteger("Energy"));

            int facing = BlockHelper.determineXZPlaceFacing(living);
            int storedFacing = ReconfigurableHelper.getFacing(stack);
            byte[] sideCache = ReconfigurableHelper.getSideCache(stack, tile.getDefaultSides());

            tile.sideCache[0] = sideCache[0];
            tile.sideCache[1] = sideCache[1];
            tile.sideCache[facing] = 0;
            tile.sideCache[BlockHelper.getLeftSide(facing)] = sideCache[BlockHelper.getLeftSide(storedFacing)];
            tile.sideCache[BlockHelper.getRightSide(facing)] = sideCache[BlockHelper.getRightSide(storedFacing)];
            tile.sideCache[BlockHelper.getOppositeSide(facing)] = sideCache[BlockHelper.getOppositeSide(storedFacing)];
        }
        super.onBlockPlacedBy(world, x, y, z, living, stack);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int hitSide, float hitX, float hitY, float hitZ) {

        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TileExtruder || tile instanceof TilePrecipitator) {
            if (FluidHelper.fillHandlerWithContainer(world, (IFluidHandler) tile, player)) {
                return true;
            }
        }
        return super.onBlockActivated(world, x, y, z, player, hitSide, hitX, hitY, hitZ);
    }

    @Override
    public int getRenderBlockPass() {

        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass) {

        renderPass = pass;
        return pass < 2;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {

        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {

        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {

        return true;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {

        ISidedTexture tile = (ISidedTexture) world.getTileEntity(x, y, z);
        return tile == null ? null : tile.getTexture(side, renderPass);
    }

    @Override
    public IIcon getIcon(int side, int metadata) {

        if (side == 0) {
            return machineBottom;
        }
        if (side == 1) {
            return machineTop;
        }
        return side != 3 ? machineSide : machineFace[metadata % Types.values().length];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {

        // Base Textures
        machineBottom = ir.registerIcon("thermalexpansion:machine/Machine_Bottom");
        machineTop = ir.registerIcon("thermalexpansion:machine/Machine_Top");
        machineSide = ir.registerIcon("thermalexpansion:machine/Machine_Side");

        // Face Textures
        for (int i = 0; i < Types.values().length; i++) {
            machineFace[i] = ir.registerIcon("thermalexpansion:machine/Machine_Face_" + StringHelper.titleCase(NAMES[i]));
            machineActive[i] = ir.registerIcon("thermalexpansion:machine/Machine_Active_" + StringHelper.titleCase(NAMES[i]));
        }

        // Config Textures
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 0, "thermalexpansion:config/Config_None", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 1, "thermalexpansion:config/Config_Blue", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 2, "thermalexpansion:config/Config_Red", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 3, "thermalexpansion:config/Config_Yellow", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 4, "thermalexpansion:config/Config_Orange", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 5, "thermalexpansion:config/Config_Green", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 6, "thermalexpansion:config/Config_Purple", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_DEFAULT + 7, "thermalexpansion:config/Config_Open", ir);

        IconRegistry.addIcon(TEProps.TEXTURE_CB + 0, "thermalexpansion:config/Config_None", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 1, "thermalexpansion:config/Config_Blue_CB", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 2, "thermalexpansion:config/Config_Red_CB", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 3, "thermalexpansion:config/Config_Yellow_CB", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 4, "thermalexpansion:config/Config_Orange_CB", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 5, "thermalexpansion:config/Config_Green_CB", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 6, "thermalexpansion:config/Config_Purple_CB", ir);
        IconRegistry.addIcon(TEProps.TEXTURE_CB + 7, "thermalexpansion:config/Config_Open", ir);
    }

    @Override
    public NBTTagCompound getItemStackTag(World world, int x, int y, int z) {

        NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
        TileMachineBase tile = (TileMachineBase) world.getTileEntity(x, y, z);

        if (tile != null) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            ReconfigurableHelper.setItemStackTagReconfig(tag, tile);
            tag.setInteger("Energy", tile.getEnergyStored(ForgeDirection.UNKNOWN));
            tile.writeAugmentsToNBT(tag);
        }
        return tag;
    }

    /* IInitializer */
    @Override
    public boolean initialize() {

        TileMachineBase.configure();
        TileFurnace.initialize();
        TilePulverizer.initialize();
        TileSawmill.initialize();
        TileSmelter.initialize();
        TileCrucible.initialize();
        TileTransposer.initialize();
        TilePrecipitator.initialize();
        TileExtruder.initialize();
        TileAccumulator.initialize();
        TileAssembler.initialize();
        TileCharger.initialize();
        TileInsolator.initialize();

        if (defaultAutoTransfer) {
            defaultAugments[0] = ItemHelper.cloneStack(TEAugments.generalAutoOutput);
        }
        if (defaultRedstoneControl) {
            defaultAugments[1] = ItemHelper.cloneStack(TEAugments.generalRedstoneControl);
        }
        if (defaultReconfigSides) {
            defaultAugments[2] = ItemHelper.cloneStack(TEAugments.generalReconfigSides);
        }
        furnace = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.FURNACE.ordinal()));
        pulverizer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.PULVERIZER.ordinal()));
        sawmill = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.SAWMILL.ordinal()));
        smelter = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.SMELTER.ordinal()));
        crucible = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.CRUCIBLE.ordinal()));
        transposer = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.TRANSPOSER.ordinal()));
        precipitator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.PRECIPITATOR.ordinal()));
        extruder = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.EXTRUDER.ordinal()));
        accumulator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.ACCUMULATOR.ordinal()));
        assembler = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.ASSEMBLER.ordinal()));
        charger = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.CHARGER.ordinal()));
        insolator = ItemBlockMachine.setDefaultTag(new ItemStack(this, 1, Types.INSOLATOR.ordinal()));

        GameRegistry.registerCustomItemStack("furnace", furnace);
        GameRegistry.registerCustomItemStack("pulverizer", pulverizer);
        GameRegistry.registerCustomItemStack("sawmill", sawmill);
        GameRegistry.registerCustomItemStack("smelter", smelter);
        GameRegistry.registerCustomItemStack("crucible", crucible);
        GameRegistry.registerCustomItemStack("transposer", transposer);
        GameRegistry.registerCustomItemStack("precipitator", precipitator);
        GameRegistry.registerCustomItemStack("extruder", extruder);
        GameRegistry.registerCustomItemStack("accumulator", accumulator);
        GameRegistry.registerCustomItemStack("assembler", assembler);
        GameRegistry.registerCustomItemStack("charger", charger);
        GameRegistry.registerCustomItemStack("insolator", insolator);

        return true;
    }

    @Override
    public boolean postInit() {

        String machineFrame = "thermalexpansion:machineFrame";
        String copperPart = "thermalexpansion:machineCopper";
        String invarPart = "thermalexpansion:machineInvar";

        // @formatter:off
        if (enable[Types.FURNACE.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(furnace, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.powerCoilGold,
                    'X', "dustRedstone",
                    'Y', Blocks.brick_block
            }));
        }
        if (enable[Types.PULVERIZER.ordinal()]) {
            String category = "Machine.Pulverizer";
            String comment = "If enabled, the Pulverizer will require Diamonds instead of Flint.";
            Item component = ThermalExpansion.config.get(category, "RequireDiamonds", false, comment) ? Items.diamond : Items.flint;
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(pulverizer, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.powerCoilGold,
                    'X', Blocks.piston,
                    'Y', component
            }));
        }
        if (enable[Types.SAWMILL.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(sawmill, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.powerCoilGold,
                    'X', Items.iron_axe,
                    'Y', "plankWood"
            }));
        }
        if (enable[Types.SMELTER.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(smelter, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', invarPart,
                    'P', TEItems.powerCoilGold,
                    'X', Items.bucket,
                    'Y', "ingotInvar"
            }));
        }
        if (enable[Types.CRUCIBLE.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(crucible, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', invarPart,
                    'P', TEItems.powerCoilGold,
                    'X', BlockFrame.frameCellBasic,
                    'Y', Blocks.nether_brick
            }));
        }
        if (enable[Types.TRANSPOSER.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(transposer, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.powerCoilGold,
                    'X', Items.bucket,
                    'Y', "blockGlass"
            }));
        }
        if (enable[Types.PRECIPITATOR.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(precipitator, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.powerCoilGold,
                    'X', Blocks.piston,
                    'Y', "ingotInvar"
            }));
        }
        if (enable[Types.EXTRUDER.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(extruder, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.pneumaticServo,
                    'X', Blocks.piston,
                    'Y', "blockGlass"
            }));
        }
        if (enable[Types.ACCUMULATOR.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(accumulator, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.pneumaticServo,
                    'X', Items.bucket,
                    'Y', "blockGlass"
            }));
        }
        if (enable[Types.ASSEMBLER.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(assembler, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.powerCoilGold,
                    'X', Blocks.chest,
                    'Y', "gearTin"
            }));
        }
        if (enable[Types.CHARGER.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(charger, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.powerCoilGold,
                    'X', BlockFrame.frameCellBasic,
                    'Y', TEItems.powerCoilSilver
            }));
        }
        if (enable[Types.INSOLATOR.ordinal()]) {
            NEIRecipeWrapper.addMachineRecipe(new RecipeMachine(insolator, defaultAugments, new Object[] {
                    " X ",
                    "YCY",
                    "IPI",
                    'C', machineFrame,
                    'I', copperPart,
                    'P', TEItems.powerCoilGold,
                    'X', "gearLumium",
                    'Y', Blocks.dirt
            }));
        }
        // @formatter:on

        TECraftingHandler.addMachineUpgradeRecipes(furnace);
        TECraftingHandler.addMachineUpgradeRecipes(pulverizer);
        TECraftingHandler.addMachineUpgradeRecipes(sawmill);
        TECraftingHandler.addMachineUpgradeRecipes(smelter);
        TECraftingHandler.addMachineUpgradeRecipes(crucible);
        TECraftingHandler.addMachineUpgradeRecipes(transposer);
        TECraftingHandler.addMachineUpgradeRecipes(precipitator);
        TECraftingHandler.addMachineUpgradeRecipes(extruder);
        TECraftingHandler.addMachineUpgradeRecipes(accumulator);
        TECraftingHandler.addMachineUpgradeRecipes(assembler);
        TECraftingHandler.addMachineUpgradeRecipes(charger);
        TECraftingHandler.addMachineUpgradeRecipes(insolator);

        TECraftingHandler.addSecureRecipe(furnace);
        TECraftingHandler.addSecureRecipe(pulverizer);
        TECraftingHandler.addSecureRecipe(sawmill);
        TECraftingHandler.addSecureRecipe(smelter);
        TECraftingHandler.addSecureRecipe(crucible);
        TECraftingHandler.addSecureRecipe(transposer);
        TECraftingHandler.addSecureRecipe(precipitator);
        TECraftingHandler.addSecureRecipe(extruder);
        TECraftingHandler.addSecureRecipe(accumulator);
        TECraftingHandler.addSecureRecipe(assembler);
        TECraftingHandler.addSecureRecipe(charger);
        TECraftingHandler.addSecureRecipe(insolator);

        return true;
    }

    public static void refreshItemStacks() {

        furnace = ItemBlockMachine.setDefaultTag(furnace);
        pulverizer = ItemBlockMachine.setDefaultTag(pulverizer);
        sawmill = ItemBlockMachine.setDefaultTag(sawmill);
        smelter = ItemBlockMachine.setDefaultTag(smelter);
        crucible = ItemBlockMachine.setDefaultTag(crucible);
        transposer = ItemBlockMachine.setDefaultTag(transposer);
        precipitator = ItemBlockMachine.setDefaultTag(precipitator);
        extruder = ItemBlockMachine.setDefaultTag(extruder);
        accumulator = ItemBlockMachine.setDefaultTag(accumulator);
        assembler = ItemBlockMachine.setDefaultTag(assembler);
        charger = ItemBlockMachine.setDefaultTag(charger);
        insolator = ItemBlockMachine.setDefaultTag(insolator);
    }

    public static enum Types {
        FURNACE, PULVERIZER, SAWMILL, SMELTER, CRUCIBLE, TRANSPOSER, PRECIPITATOR, EXTRUDER, ACCUMULATOR, ASSEMBLER, CHARGER, INSOLATOR
    }

    public static IIcon machineBottom;
    public static IIcon machineTop;
    public static IIcon machineSide;

    public static IIcon[] machineFace = new IIcon[Types.values().length];
    public static IIcon[] machineActive = new IIcon[Types.values().length];

    public static final String[] NAMES = { "furnace", "pulverizer", "sawmill", "smelter", "crucible", "transposer", "precipitator", "extruder", "accumulator",
            "assembler", "charger", "insolator" };
    public static boolean[] enable = new boolean[Types.values().length];
    public static boolean[] creativeTiers = new boolean[4];
    public static ItemStack[] defaultAugments = new ItemStack[3];

    public static boolean defaultAutoTransfer = true;
    public static boolean defaultRedstoneControl = true;
    public static boolean defaultReconfigSides = true;

    static {
        String category = "Machine.";

        for (int i = 0; i < Types.values().length; i++) {
            enable[i] = ThermalExpansion.config.get(category + StringHelper.titleCase(NAMES[i]), "Recipe.Enable", true);
        }
        category = "Machine.All";

        creativeTiers[0] = ThermalExpansion.config.get(category, "CreativeTab.Basic", false);
        creativeTiers[1] = ThermalExpansion.config.get(category, "CreativeTab.Hardened", false);
        creativeTiers[2] = ThermalExpansion.config.get(category, "CreativeTab.Reinforced", false);
        creativeTiers[3] = ThermalExpansion.config.get(category, "CreativeTab.Resonant", true);

        category += ".Augments";

        defaultAutoTransfer = ThermalExpansion.config.get(category, "Default.AutoTransfer", true);
        defaultRedstoneControl = ThermalExpansion.config.get(category, "Default.RedstoneControl", true);
        defaultReconfigSides = ThermalExpansion.config.get(category, "Default.ReconfigurableSides", true);
    }

    public static ItemStack furnace;
    public static ItemStack pulverizer;
    public static ItemStack sawmill;
    public static ItemStack smelter;
    public static ItemStack crucible;
    public static ItemStack transposer;
    public static ItemStack precipitator;
    public static ItemStack extruder;
    public static ItemStack accumulator;
    public static ItemStack assembler;
    public static ItemStack charger;
    public static ItemStack insolator;

}
    Contact GitHub API Training Shop Blog About
        © 2016 GitHub, Inc. Terms Privacy Security Status Help