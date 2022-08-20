package thecsdev.nounusedchunks.client.mixin;

import static thecsdev.nounusedchunks.config.NUCConfig.OW_RUC;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.BackupPromptScreen.Callback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import thecsdev.nounusedchunks.client.gui.widgets.ActionCheckboxWidget;
import thecsdev.nounusedchunks.config.NUCConfig;

@Mixin(BackupPromptScreen.class)
public abstract class BackupPromptScreenMixin extends Screen
{
	// ==================================================
	protected BackupPromptScreenMixin(Text title) { super(title); }
	// ==================================================
	public ActionCheckboxWidget removeUnusedChunksCheckbox;
	// --------------------------------------------------
	@Accessor("showEraseCacheCheckbox")
	public abstract boolean getShowEraseCacheCheckbox();
	
	@Accessor("eraseCacheCheckbox")
	public abstract CheckboxWidget getEraseCacheCheckbox();
	
	@Accessor("callback")
	public abstract Callback getCallback();
	// ==================================================
	@Inject(method = "init", at = @At("TAIL"))
	public void init(CallbackInfo callback)
	{
		//reset temp. values:
		OW_RUC = false;
		
		//define the check-box
		int i = getEraseCacheCheckbox().y, j = getEraseCacheCheckbox().getHeight() + 5;
		removeUnusedChunksCheckbox = new ActionCheckboxWidget(
				this.width / 2 - 155 + 80, i + j,
				150, 20,
				new TranslatableText("nounusedchunks.backupprompt.removeunusedchunks"), OW_RUC, true,
				checkbox ->
				{
					OW_RUC = checkbox.isChecked();
					NUCConfig.saveProperties();
				});
		
		//if the game is showing the check-boxes, add the check-box below the
		//vanilla one, and move the buttons down
		if(getShowEraseCacheCheckbox())
		{
			//add the check-box
			addDrawableChild(removeUnusedChunksCheckbox);
			
			//move the stuff such as buttons and other UI elements below it a little bit down
			((ScreenMixin)(Object)this).getDrawables().forEach(drawable ->
			{
				//ignore non-clickables,
				//ignore the removeUnusedChunks checkbox
				if(!(drawable instanceof ClickableWidget) || drawable == removeUnusedChunksCheckbox)
					return;
				ClickableWidget cw = (ClickableWidget)drawable;
				
				//ignore above ones
				if(cw.y < removeUnusedChunksCheckbox.y - 5)
					return;
				
				//move a bit down
				cw.y += j + 10;
			});
		}
	}
	// ==================================================
}