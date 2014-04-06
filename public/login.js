var login = {};

login.loginId = '#log_in';
login.password = '#password';
login.login = '#login';
login.noUser = '#no_user';
login.trainerButtonReg = '#trainer-btn-reg';
login.userButtonReg = '#user-btn-reg';
login.trainerReg = '#trainer-registration';
login.userReg = '#user-registration';

login.init = function () {
	try {
		$(login.noUser).addClass('hidden');
		$(login.noUser).removeClass('show');
		$(login.trainerReg).addClass('hidden');
		$(login.trainerReg).removeClass('show');
		$(login.userReg).addClass('hidden');
		$(login.userReg).removeClass('show');		
		$(login.loginId).click(login.submit);
		$(login.trainerButtonReg).click(login.trainerShowReg);
		$(login.userButtonReg).click(login.userShowReg);
		return true;
	} catch (e) {
		console.error(e);
		return false;
	}
}

login.submit = function(){
	try {		
		$(location).attr('href', 'main.html');			
		return true;
	} catch (e) {
		console.error(e);
		return false;
	}
}

login.trainerShowReg = function(){
	try {		
		$(login.userReg).addClass('hidden');
		$(login.userReg).removeClass('show');
		$(login.trainerReg).addClass('show');
		$(login.trainerReg).removeClass('hidden');	
		//console.log("train");		
		return true;
	} catch (e) {
		console.error(e);
		return false;
	}
}

login.userShowReg = function(){
	try {		
		$(login.userReg).addClass('show');
		$(login.userReg).removeClass('hidden');
		$(login.trainerReg).addClass('hidden');
		$(login.trainerReg).removeClass('show');	
		//console.log("user");
		return true;
	} catch (e) {
		console.error(e);
		return false;
	}
}